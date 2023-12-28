package cc.unitmesh.pick.builder.unittest.java

import cc.unitmesh.core.ast.NodeIdentifier
import cc.unitmesh.core.ast.NodeType
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.builder.unittest.base.BasicTestIns
import cc.unitmesh.pick.ext.checkNamingStyle
import cc.unitmesh.pick.ext.toSourceCode
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

/**
 * 为给定的 CodeDataStruct 的每个 CodeFunction 生成测试指令。
 *
 * @property context 生成测试指令的上下文
 */
class JavaMethodTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {

    /**
     * 针对给定的 [CodeDataStruct] 的每个 [CodeFunction] 生成 [BasicTestIns]
     *
     * @param dataStruct 要生成测试指令的 [CodeDataStruct]
     * @param underTestFile 表示被测试的文件的 [CodeDataStruct]
     * @param relevantClasses 表示相关类的 [CodeDataStruct] 列表
     * @return 返回生成的 [BasicTestIns]
     *
     * 主要处理逻辑：
     *
     * 1. 判断给定的 [CodeDataStruct] 是否符合质量阈值，如果不符合则返回空列表。
     * 2. 从 [CodeDataStruct] 中提取出所有的 [CodeFunction]，并对每个 [CodeFunction] 进行如下操作：
     *    - 从测试代码中提取出所有的函数调用，对于每个函数调用，如果函数调用的 [CodeDataStruct] 不是 [underTestFile]，则跳过。
     *    - 如果函数调用的 [CodeDataStruct] 是 [underTestFile]，则提取出原始函数的代码内容，生成 [BasicTestIns]。
     *    - 将生成的 [BasicTestIns] 放入结果列表中
     * 额外逻辑：
     * 1. 生成命名风格，以便于后续生成测试代码时使用。
     */
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
                val canonicalName = it.Package + "." + it.NodeName + ":" + it.FunctionName

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
}
