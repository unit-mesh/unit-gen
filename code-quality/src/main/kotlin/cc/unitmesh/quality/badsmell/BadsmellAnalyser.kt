package cc.unitmesh.quality.badsmell

import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodePosition
import chapi.domain.core.DataStructType
import org.archguard.rule.core.Issue

private val CodeFunction.IfSize: Int get() = 0
private val CodeFunction.SwitchSize: Int get() = 0
private val CodeFunction.IfInfo: List<CodePosition> get() = listOf()

/**
 * The `BadsmellAnalyser` class is responsible for performing analysis on a list of `CodeDataStruct` objects and
 * identifying various code smells.
 * It implements the `QualityAnalyser` interface.
 *
 * @property bsThresholds The thresholds for each code smell, used for comparison during the analysis.
 * @constructor Creates a `BadsmellAnalyser` instance with the given thresholds. If no thresholds are provided,
 * default thresholds are used.
 */
class BadsmellAnalyser(thresholds: Map<String, Int> = BsThresholds().toThresholds()) : QualityAnalyser {
    private var bsThresholds = BsThresholds()

    init {
        this.bsThresholds = bsThresholds.from(thresholds)
    }

    /**
     * This method performs analysis on a list of CodeDataStruct objects and identifies various code smells.
     * The identified code smells include:
     *
     * - LongMethod: A code smell that occurs when a method is excessively long and difficult to understand or maintain.
     * - LongParameterList: A code smell that occurs when a method has a large number of parameters, which can make the method harder to use and understand.
     * - ComplexCondition: A code smell that occurs when a method has complex conditional statements, which can make the code harder to read and maintain.
     * - DataClass: A code smell that occurs when a class is used only to hold data without any additional behavior or functionality.
     * - RefusedBequest: A code smell that occurs when a subclass inherits methods or properties from a superclass that it does not need or use.
     * - LargeClass: A code smell that occurs when a class has grown too large and contains too many responsibilities, making it difficult to understand and maintain.
     *
     * @param nodes The list of CodeDataStruct objects to be analyzed.
     * @return A list of Issue objects representing the identified code smells.
     */
    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        val badSmellList = mutableListOf<BadSmellModel>()
        for (node in nodes) {
            checkLazyElement(node, badSmellList)

            var onlyHaveGetterAndSetter = true
            for (method in node.Functions) {
                checkLongMethod(method, node, badSmellList)

                if (!(method.isGetterSetter())) {
                    onlyHaveGetterAndSetter = false
                }

                checkLongParameterList(method, node, badSmellList)
                // Todo: Custom with new API
                //  checkRepeatedSwitches(method, node, badSmellList)
                // Todo: Custom with new API
                checkComplexIf(method, node, badSmellList)
            }

            checkDataClass(onlyHaveGetterAndSetter, node, badSmellList)
            // Todo: Custom with new API
            checkRefusedBequest(node, badSmellList)
            checkLargeClass(node, badSmellList)
        }

        checkConnectedGraphCall(nodes, badSmellList)
        return badSmellList.map { it.toIssue() }.toMutableList()
    }

    private fun checkConnectedGraphCall(nodes: List<CodeDataStruct>, badSmellList: MutableList<BadSmellModel>) {
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

    private fun CodeDataStruct.getCalledClasses(maps: Map<String, Boolean>): List<String> {
        val calledClassesMap = mutableMapOf<String, Boolean>()
        val calledClasses = mutableListOf<String>()
        for (methodCalled in this.FunctionCalls) {
            val hasExistFunction = maps[methodCalled.buildClassFullName()] ?: continue

            if (methodCalled.NodeName == "" || hasExistFunction || this.getClassFullName() == methodCalled.buildClassFullName()) {
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

        if (methodLength > bsThresholds.bsMethodLength) {
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
        if (node.Type == DataStructType.CLASS && normalClassLength >= bsThresholds.bsLargeLength) {
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
        for (info in method.IfInfo) {
            if (info.StopLine - info.StartLine >= bsThresholds.bsIfLinesLength) {
                val longParams = BadSmellModel(
                    file = node.FilePath,
                    line = info.StartLine.toString(),
                    bs = SmellType.SMELL_COMPLEX_CONDITION,
                    description = SmellType.SMELL_COMPLEX_CONDITION.name
                )

                badSmellList.add(longParams)
            }
        }
    }

    fun checkRepeatedSwitches(method: CodeFunction, node: CodeDataStruct, badSmellList: MutableList<BadSmellModel>) {
        if (method.IfSize >= bsThresholds.bsIfSwitchLength) {
            val longParams = BadSmellModel(
                file = node.FilePath,
                line = method.Position.StartLine.toString(),
                bs = SmellType.SMELL_REPEATED_SWITCHES,
                description = "ifSize",
                size = method.IfSize
            )
            badSmellList.add(longParams)
        }

        if (method.SwitchSize >= bsThresholds.bsIfSwitchLength) {
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
        if (method.Parameters.size > bsThresholds.bsLongParasLength) {
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