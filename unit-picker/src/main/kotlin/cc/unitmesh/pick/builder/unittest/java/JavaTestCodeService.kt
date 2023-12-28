package cc.unitmesh.pick.builder.unittest.java

import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.builder.unittest.base.UnitTestService
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodeImport

open class JavaTestCodeService(open val context: JobContext) : UnitTestService {
    override fun isApplicable(dataStruct: CodeDataStruct): Boolean {
        return dataStruct.NodeName.endsWith("Test") || dataStruct.NodeName.endsWith("Tests")
    }

    /**
     * 寻找给定 CodeDataStruct 的被测试文件。通过 NodeName 在 fileTree 中搜索，如果找到了被测试文件，则返回。
     *
     * @param dataStruct 整个类信息
     * @return 返回被测试文件的列表
     */
    fun findUnderTestFile(dataStruct: CodeDataStruct): List<CodeDataStruct> {
        val fileTree = context.fileTree
        val testClass = dataStruct.NodeName.removeSuffix("Test").removeSuffix("Tests")

        if (testClass == dataStruct.NodeName) {
            return emptyList()
        }

        return fileTree.values.mapNotNull { fileJob ->
            fileJob.container?.DataStructures?.find {
                it.NodeName == testClass
            }
        }
    }

    /**
     * 寻找给定 CodeDataStruct 的相关类，通过分析其 Imports 语法，如果在 fileTree 中找到了相关类，则返回。
     *
     * @param dataStruct 整个类信息
     * @return 返回相关类的列表
     */
    fun lookupRelevantClass(dataStruct: CodeDataStruct): List<CodeDataStruct> {
        val fileTree = context.fileTree
        val imports = dataStruct.Imports
        return imports.mapNotNull {
            fileTree[it.Source]?.container?.DataStructures
        }.flatten()
    }

    /**
     * 寻找给定 CodeDataStruct 的相关类，通过分析其 Imports 语法，如果在 fileTree 中找到了相关类，则返回。
     * 同时，还会分析给定的 CodeFunction 的返回值和参数，如果在 fileTree 中找到了相关类，则返回。
     *
     * @param codeFunction 待查找的函数
     * @param dataStruct 整个类信息
     *
     * @return 返回相关类的列表
     */
    fun lookupRelevantClass(codeFunction: CodeFunction, dataStruct: CodeDataStruct): List<CodeDataStruct> {
        val fileTree = context.fileTree
        val returnType = codeFunction.ReturnType

        val imports = dataStruct.Imports
        val outboundType = filterDs(imports, returnType, fileTree)


        val inboundsType = codeFunction.Parameters.map {
            filterDs(imports, it.TypeType, fileTree)
        }.flatten()

        return outboundType + inboundsType
    }

    fun filterDs(
        imports: List<CodeImport>,
        returnType: String,
        fileTree: HashMap<String, InstructionFileJob>,
    ) = imports.mapNotNull {
        if (it.Source.endsWith(returnType)) {
            fileTree[it.Source]?.container?.DataStructures
        } else {
            null
        }
    }.flatten()

    override fun build(dataStruct: CodeDataStruct): List<TypedTestIns> {
        val underTestFile = this.findUnderTestFile(dataStruct).firstOrNull() ?: return emptyList()
        val relevantClasses = this.lookupRelevantClass(dataStruct)
        val classTestIns = ClassTestCodeBuilder(context)
            .build(dataStruct, underTestFile, relevantClasses)

        val methodTests = JavaMethodTestCodeBuilder(context)
            .build(dataStruct, underTestFile, relevantClasses)

        return classTestIns + methodTests
    }
}

