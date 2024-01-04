package cc.unitmesh.pick.builder.unittest.kotlin

import cc.unitmesh.core.ast.NodeIdentifier
import cc.unitmesh.core.ast.NodeType
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.builder.unittest.base.BasicTestIns
import cc.unitmesh.pick.spec.checkNamingStyle
import cc.unitmesh.pick.ext.toSourceCode
import cc.unitmesh.pick.ext.toUml
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.DataStructType

/**
 * 为给定的 CodeDataStruct 的每个 CodeFunction 生成测试指令。
 *
 * @property context 生成测试指令的上下文
 */
class KotlinMethodTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {

    /**
     * 结合给定的Kotlin类、被测试的Kotlin类、相关类生成测试指令。
     *
     * 主要逻辑：
     * 1. 判断给定的 [CodeDataStruct] 是否符合质量阈值，如果不符合则返回空列表。
     * 2. 从 [CodeDataStruct] 中提取出所有的 [CodeFunction]，并对每个 [CodeFunction] 进行如下操作：
     *   - 从测试代码中提取出所有的函数调用，对于每个函数调用，如果函数调用的 [CodeDataStruct] 不是 [underTestFile]，则跳过。
     *   - 如果函数调用的 [CodeDataStruct] 是 [underTestFile]，则提取出原始函数的代码内容，生成 [BasicTestIns]。
     *
     * @param dataStruct The test class, which is the current class.
     * @param underTestFile The class to be tested.
     * @param relevantClasses A list of relevant classes.
     * @return A list of basic test instructions.
     */
    override fun build(
        /// 测试类，即当前类
        dataStruct: CodeDataStruct,
        /// 待测试类
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
            val content = function.Content
            val dataStructType = underTestFile.Type

            underTestFile.Functions.map {

                val isNotClassCall = dataStructType == DataStructType.CLASS && !content.contains("." + it.Name + "(")
                val isNotObjectCall = dataStructType == DataStructType.OBJECT && !content.contains(it.Name + "(")
                if (isNotClassCall || isNotObjectCall) {
                    return@map
                }

                val testIns = BasicTestIns(
                    identifier = NodeIdentifier(
                        type = NodeType.METHOD,
                        name = underTestFile.NodeName + " Class' " + it.Name,
                    ),
                    language = context.project.language,
                    underTestCode = it.Content,
                    generatedCode = content,
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
