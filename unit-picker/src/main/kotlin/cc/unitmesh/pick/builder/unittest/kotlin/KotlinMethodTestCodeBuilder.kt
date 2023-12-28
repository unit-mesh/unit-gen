package cc.unitmesh.pick.builder.unittest.kotlin

import cc.unitmesh.core.ast.NodeIdentifier
import cc.unitmesh.core.ast.NodeType
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.builder.unittest.base.BasicTestIns
import cc.unitmesh.pick.ext.checkNamingStyle
import cc.unitmesh.pick.ext.toSourceCode
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeCall
import chapi.domain.core.CodeDataStruct

/**
 * 为给定的 CodeDataStruct 的每个 CodeFunction 生成测试指令。
 *
 * @property context 生成测试指令的上下文
 */
class KotlinMethodTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {

    override fun build(
        dataStruct: CodeDataStruct,
        underTestFile: CodeDataStruct,
        relevantClasses: List<CodeDataStruct>,
    ): List<BasicTestIns> {
        val generatedCode = dataStruct.toSourceCode()
        if (generatedCode.lines().size > context.insQualityThreshold.maxLineInCode) return emptyList()

        val namingStyle = dataStruct.checkNamingStyle()
        val results: HashMap<String, List<BasicTestIns>> = hashMapOf()

        val underTestFunctionMap = underTestFile.Functions.associate {
            val canonicalName = underTestFile.Package + "." + underTestFile.NodeName + ":" + it.Name
            canonicalName to it.Content
        }

        // 分析测试代码，找到原始函数的代码内容，放到结果中
        dataStruct.Functions.mapIndexed { _, function ->
            function.FunctionCalls.map {
                val canonicalName = if (it.Package == "") {
                    lookupCanonicalName(context.fileTree, dataStruct, it) ?: return@map
                } else {
                    it.Package + "." + it.NodeName + ":" + it.FunctionName
                }

                if (it.NodeName != underTestFile.NodeName) return@map
                val originalContent = underTestFunctionMap[canonicalName] ?: return@map
                if (originalContent.isBlank() || function.Content.isBlank()) return@map

                val testIns = BasicTestIns(
                    identifier = NodeIdentifier(
                        type = NodeType.METHOD,
                        name = it.FunctionName,
                    ),
                    language = context.project.language,
                    underTestCode = originalContent,
                    generatedCode = function.Content,
                    coreFrameworks = context.project.coreFrameworks,
                    testFrameworks = context.project.testFrameworks,
                    testType = TestCodeBuilderType.METHOD_UNIT,
                    specs = listOf(
                        "Test class should be named `${namingStyle}`."
                    )
                )

                results[canonicalName] = results[canonicalName]?.plus(testIns) ?: listOf(testIns)
            }
        }

        return results.values.flatten()
    }

    private fun lookupCanonicalName(
        fileTree: java.util.HashMap<String, InstructionFileJob>,
        testFile: CodeDataStruct,
        codeCall: CodeCall,
    ): String? {
        val maybeCreator = codeCall.FunctionName[0].isUpperCase()
        if (maybeCreator) {
           val pkg = testFile.Package
            val nodeName = pkg + "." + codeCall.FunctionName
            val node = fileTree[nodeName]?.container?.DataStructures?.firstOrNull() ?: return null

            codeCall.Package = node.Package
            codeCall.NodeName = node.NodeName
            codeCall.FunctionName = "PrimaryConstructor"

            return node.Package + "." + node.NodeName + ":" + "PrimaryConstructor"
        }

        return null
    }
}
