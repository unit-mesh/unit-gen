package cc.unitmesh.pick.builder.unittest.kotlin

import cc.unitmesh.core.ast.NodeIdentifier
import cc.unitmesh.core.ast.NodeType
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.builder.unittest.base.BasicTestIns
import cc.unitmesh.pick.ext.checkNamingStyle
import cc.unitmesh.pick.ext.toSourceCode
import cc.unitmesh.pick.ext.toUml
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

/**
 * 为给定的 CodeDataStruct 的每个 CodeFunction 生成测试指令。
 *
 * @property context 生成测试指令的上下文
 */
class KotlinMethodTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {

    override fun build(
        // 测试类，即当前类
        dataStruct: CodeDataStruct,
        // 待测试类
        underTestFile: CodeDataStruct,
        relevantClasses: List<CodeDataStruct>,
    ): List<BasicTestIns> {
        val generatedCode = dataStruct.toSourceCode()
        if (generatedCode.lines().size > context.insQualityThreshold.maxLineInCode) return emptyList()

        val namingStyle = dataStruct.checkNamingStyle()
        val results: HashMap<String, List<BasicTestIns>> = hashMapOf()

        val currentUml = "\n// Current file:\n ${underTestFile.toUml()}"

        // 分析测试代码，找到原始函数的代码内容，放到结果中
        dataStruct.Functions.mapIndexed { _, function ->
            underTestFile.Functions.map {
                if (!function.Content.contains("." + it.Name + "(")) {
                    return@map
                }

                val testIns = BasicTestIns(
                    identifier = NodeIdentifier(
                        type = NodeType.METHOD,
                        name = underTestFile.NodeName + " Class' " + it.Name,
                    ),
                    language = context.project.language,
                    underTestCode = it.Content,
                    generatedCode = function.Content,
                    coreFrameworks = context.project.coreFrameworks,
                    testFrameworks = context.project.testFrameworks,
                    testType = TestCodeBuilderType.METHOD_UNIT,
                    relatedCode = listOf(currentUml),
                    specs = listOf(
                        "Test class should be named `${namingStyle}`."
                    )
                )

                results[it.Name] = listOf(testIns)
            }
        }

        return results.values.flatten()
    }
}
