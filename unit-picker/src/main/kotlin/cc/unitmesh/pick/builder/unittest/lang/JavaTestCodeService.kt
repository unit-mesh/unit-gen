package cc.unitmesh.pick.builder.unittest.lang

import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.builder.testBuilders
import cc.unitmesh.pick.builder.unittest.ClassTestCodeBuilder
import cc.unitmesh.pick.builder.unittest.MethodTestCodeBuilder
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodeImport

class JavaTestCodeService(val context: JobContext) : UnitTestService {
    override fun isApplicable(dataStruct: CodeDataStruct): Boolean {
        return dataStruct.NodeName.endsWith("Test") || dataStruct.NodeName.endsWith("Tests")
    }

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

    fun lookupRelevantClass(dataStruct: CodeDataStruct): List<CodeDataStruct> {
        val fileTree = context.fileTree
        val imports = dataStruct.Imports
        return imports.mapNotNull {
            fileTree[it.Source]?.container?.DataStructures
        }.flatten()
    }

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
        if (!context.completionBuilderTypes.contains(CompletionBuilderType.TEST_CODE_GEN)) {
            return emptyList()
        }

        val underTestFile = this.findUnderTestFile(dataStruct).firstOrNull() ?: return emptyList()
        val relevantClasses = this.lookupRelevantClass(dataStruct)
        val classTestIns = ClassTestCodeBuilder(context)
            .build(dataStruct, underTestFile, relevantClasses)

        val methodTests = MethodTestCodeBuilder(context)
            .build(dataStruct, underTestFile, relevantClasses)

        return classTestIns + methodTests
    }
}

