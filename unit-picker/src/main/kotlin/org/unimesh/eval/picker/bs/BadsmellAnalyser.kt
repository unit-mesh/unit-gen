package org.unimesh.eval.picker.bs

import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodePosition
import chapi.domain.core.DataStructType

private val CodeFunction.IfSize: Int get() = 0
private val CodeFunction.SwitchSize: Int get() = 0
private val CodeFunction.IfInfo: CodePosition
    get() {
        return CodePosition(StartLine = this.Position.StartLine, StopLine = this.Position.StopLine)
    }

data class BsConfig(
    val bsLongParasLength: Int = 5,
    val bsIfSwitchLength: Int = 8,
    val bsLargeLength: Int = 20,
    val bsMethodLength: Int = 30,
    val bsIfLinesLength: Int = 3,
)

enum class SmellType {
    SMELL_GARPH_CONNECTED_CALL,
    SMELL_LAZY_ELEMENT,
    SMELL_LONG_METHOD,
    SMELL_DATA_CLASS,
    SMELL_REFUSED_BEQUEST,
    SMELL_LARGE_CLASS,
    SMELL_COMPLEX_CONDITION,
    SMELL_REPEATED_SWITCHES,
    SMELL_LONG_PARAMETER_LIST
}

data class BadSmellModel(
    val file: String? = null,
    val line: String? = null,
    val bs: SmellType? = null,
    val description: String? = null,
    val size: Int? = null,
)


class BadsmellAnalyser(val data: List<CodeDataStruct>, val bsConfig: BsConfig = BsConfig()) {
    fun analysis(): MutableList<BadSmellModel> {
        val badSmellList = mutableListOf<BadSmellModel>()
        for (node in data) {
            checkLazyElement(node, badSmellList)

            var onlyHaveGetterAndSetter = true
            for (method in node.Functions) {
                checkLongMethod(method, node, badSmellList)

                if (!(method.isGetterSetter())) {
                    onlyHaveGetterAndSetter = false
                }

                checkLongParameterList(method, node, badSmellList)
                checkRepeatedSwitches(method, node, badSmellList)
                checkComplexIf(method, node, badSmellList)
            }

            checkDataClass(onlyHaveGetterAndSetter, node, badSmellList)
            checkRefusedBequest(node, badSmellList)
            checkLargeClass(node, badSmellList)
        }

        checkConnectedGraphCall(data, badSmellList)
        return badSmellList
    }

    fun checkConnectedGraphCall(nodes: List<CodeDataStruct>, badSmellList: MutableList<BadSmellModel>) {
        val classNodes = mutableMapOf<String, List<String>>()
        val classNodeMaps = mutableMapOf<String, Boolean>()
        for (node in nodes) {
            classNodeMaps[node.getClassFullName()] = true
        }
        for (node in nodes) {
            classNodes[node.getClassFullName()] = node.getCalledClasses(classNodeMaps)
        }
        val badSmellGraphCall = BadSmellGraphCall()
        val descriptions = badSmellGraphCall.analysisGraphCallPath(classNodes)
        for (description in descriptions) {
            badSmellList.add(BadSmellModel(bs = SmellType.SMELL_GARPH_CONNECTED_CALL, description = description))
        }
    }

    fun CodeDataStruct.getCalledClasses(maps: Map<String, Boolean>): List<String> {
        val calledClassesMap = mutableMapOf<String, Boolean>()
        val calledClasses = mutableListOf<String>()
        for (methodCalled in this.FunctionCalls) {
            if (methodCalled.NodeName == "" || !maps[methodCalled.buildClassFullName()]!! || this.getClassFullName() == methodCalled.buildClassFullName()) {
                continue
            }
            calledClassesMap[methodCalled.buildClassFullName()] = true
        }
        for (key in calledClassesMap.keys) {
            calledClasses.add(key)
        }

        return calledClasses
    }


    fun checkLazyElement(node: CodeDataStruct, badSmellList: MutableList<BadSmellModel>) {
        if (node.Type == DataStructType.CLASS && node.Functions.isEmpty()) {
            badSmellList.add(BadSmellModel(file = node.FilePath, bs = SmellType.SMELL_LAZY_ELEMENT))
        }
    }

    fun checkLongMethod(method: CodeFunction, node: CodeDataStruct, badSmellList: MutableList<BadSmellModel>) {
        val methodLength = method.Position.StopLine - method.Position.StartLine

        if (methodLength > bsConfig.bsMethodLength) {
            val description = "method length: $methodLength"
            val longMethod = BadSmellModel(
                file = node.FilePath,
                line = method.Position.StartLine.toString(),
                bs = SmellType.SMELL_LONG_METHOD,
                description = description,
                size = methodLength
            )
            badSmellList.add(longMethod)
        }
    }

    fun checkDataClass(
        onlyHaveGetterAndSetter: Boolean,
        node: CodeDataStruct,
        badSmellList: MutableList<BadSmellModel>,
    ) {
        if (onlyHaveGetterAndSetter && node.Type == DataStructType.CLASS && node.Functions.isNotEmpty()) {
            val dataClass =
                BadSmellModel(file = node.FilePath, bs = SmellType.SMELL_DATA_CLASS, size = node.Functions.size)
            badSmellList.add(dataClass)
        }
    }

    fun checkRefusedBequest(node: CodeDataStruct, badSmellList: MutableList<BadSmellModel>) {
        if (node.Extend != "" && node.HasCallSuper()) {
            badSmellList.add(BadSmellModel(file = node.FilePath, bs = SmellType.SMELL_REFUSED_BEQUEST))
        }
    }

    fun CodeDataStruct.HasCallSuper(): Boolean {
        var hasCallSuperMethod = false
        for (methodCall in this.FunctionCalls) {
            if (methodCall.NodeName == this.Extend) {
                hasCallSuperMethod = true
            }
        }

        return hasCallSuperMethod
    }

    fun checkLargeClass(node: CodeDataStruct, badSmellList: MutableList<BadSmellModel>) {
        val normalClassLength = node.Functions.filter { !it.isGetterSetter() }.size
        if (node.Type == DataStructType.CLASS && normalClassLength >= bsConfig.bsLargeLength) {
            val description = "methods number (without getter/setter): $normalClassLength"
            badSmellList.add(
                BadSmellModel(
                    file = node.FilePath,
                    bs = SmellType.SMELL_LARGE_CLASS,
                    description = description,
                    size = normalClassLength
                )
            )
        }
    }

    fun checkComplexIf(method: CodeFunction, node: CodeDataStruct, badSmellList: MutableList<BadSmellModel>) {
        val info = method.IfInfo
        if (info.StopLine - info.StartLine >= bsConfig.bsIfLinesLength) {
            val longParams = BadSmellModel(
                file = node.FilePath,
                line = info.StartLine.toString(),
                bs = SmellType.SMELL_COMPLEX_CONDITION,
                description = SmellType.SMELL_COMPLEX_CONDITION.name
            )
            badSmellList.add(longParams)
        }
    }

    fun checkRepeatedSwitches(method: CodeFunction, node: CodeDataStruct, badSmellList: MutableList<BadSmellModel>) {
        if (method.IfSize >= bsConfig.bsIfSwitchLength) {
            val longParams = BadSmellModel(
                file = node.FilePath,
                line = method.Position.StartLine.toString(),
                bs = SmellType.SMELL_REPEATED_SWITCHES,
                description = "ifSize",
                size = method.IfSize
            )
            badSmellList.add(longParams)
        }

        if (method.SwitchSize >= bsConfig.bsIfSwitchLength) {
            val longParams = BadSmellModel(
                file = node.FilePath,
                line = method.Position.StartLine.toString(),
                bs = SmellType.SMELL_REPEATED_SWITCHES,
                description = "switchSize",
                size = method.SwitchSize
            )
            badSmellList.add(longParams)
        }
    }

    fun checkLongParameterList(method: CodeFunction, node: CodeDataStruct, badSmellList: MutableList<BadSmellModel>) {
        if (method.Parameters.size > bsConfig.bsLongParasLength) {
            val paramsJson = method.Parameters.joinToString(", ")
            val longParams = BadSmellModel(
                file = node.FilePath,
                line = method.Position.StartLine.toString(),
                bs = SmellType.SMELL_LONG_PARAMETER_LIST,
                description = paramsJson,
                size = method.Parameters.size
            )
            badSmellList.add(longParams)
        }
    }
}