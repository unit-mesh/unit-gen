package cc.unitmesh.pick.builder.unittest.java

import cc.unitmesh.core.ast.NodeIdentifier
import cc.unitmesh.core.ast.NodeType
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.pick.builder.unittest.base.BasicTestIns
import cc.unitmesh.pick.ext.checkNamingStyle
import cc.unitmesh.pick.ext.toSourceCode
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

/**
 * 为给定的 CodeDataStruct 生成对应类级别的测试 Instruction。
 *
 * @property context the job context for the test code generation
 * @constructor Creates a `ClassTestCodeBuilder` with the specified job context.
 * @param context the job context for the test code generation
 */
class ClassTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {
    /**
     * 为给定的 CodeDataStruct 生成测试指令。
     *
     * @param dataStruct is the test file
     * @param underTestFile is the file under test
     * @param relevantClasses are the classes relevant to the test file
     * @return 返回生成的 [BasicTestIns]
     *
     * 主要处理逻辑：
     * 1. 判断给定的 [CodeDataStruct] 是否符合质量阈值，如果不符合则返回空列表。
     * 2. 直接生成 [BasicTestIns]。
     * 额外逻辑：
     * 1. 生成命名风格，以便于后续生成测试代码时使用。
     */
    override fun build(
        dataStruct: CodeDataStruct,
        underTestFile: CodeDataStruct,
        relevantClasses: List<CodeDataStruct>,
    ): List<BasicTestIns> {
        val generatedCode = dataStruct.toSourceCode()
        if (generatedCode.lines().size > context.insQualityThreshold.maxLineInCode) {
            return emptyList()
        }

        val namingStyle = dataStruct.checkNamingStyle()

        return listOf(
            BasicTestIns(
                identifier = NodeIdentifier(
                    type = NodeType.CLASS,
                    name = underTestFile.NodeName,
                ),
                language = context.project.language,
                underTestCode = underTestFile.Content,
                generatedCode = generatedCode,
                coreFrameworks = context.project.coreFrameworks,
                testFrameworks = context.project.testFrameworks,
                testType = TestCodeBuilderType.CLASS_UNIT,
                specs = listOf(
                    "Test class should be named `${namingStyle}`."
                )
            )
        )
    }
}
