package cc.unitmesh.pick.strategy.lang

import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodeImport

class JavaTestCodeService(val context: JobContext) : UnitTestService {
    override fun findUnderTestFile(dataStruct: CodeDataStruct): List<CodeDataStruct> {
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

    override fun lookupRelevantClass(dataStruct: CodeDataStruct): List<CodeDataStruct> {
        val fileTree = context.fileTree
        val imports = dataStruct.Imports
        return imports.mapNotNull {
            fileTree[it.Source]?.container?.DataStructures
        }.flatten()
    }

    override fun lookupRelevantClass(codeFunction: CodeFunction, dataStruct: CodeDataStruct): List<CodeDataStruct> {
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
}

