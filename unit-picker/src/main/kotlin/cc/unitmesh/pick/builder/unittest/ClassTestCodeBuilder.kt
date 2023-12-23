package cc.unitmesh.pick.builder.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

class ClassTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {
    /**
     * @param dataStruct is the test file
     * @param underTestFile is the file under test
     * @param relevantClasses are the classes relevant to the test file
     */
    override fun build(
        dataStruct: CodeDataStruct,
        underTestFile: CodeDataStruct,
        relevantClasses: List<CodeDataStruct>,
    ): List<ClassTestIns> {
        return listOf(
            ClassTestIns(
                coreFrameworks = listOf(),
                testType = TestCodeBuilderType.CLASS_UNIT,
                specs = listOf(
                    "You MUST use should_xx_xx style for test method name.",
                    "You MUST use given-when-then style.",
                    "Test file should be complete and compilable, without need for further actions.",
                    "Instead of using `@BeforeEach` methods for setup, include all necessary code initialization within each individual test method, do not write parameterized tests."
                ),
                additionalRules = listOf()
            )
        )
    }

}

class ClassTestIns(
    val coreFrameworks: List<String> = listOf(),
    val testFrameworks: List<String> = listOf(),
    /**
     * the Specification of the test
     */
    val specs: List<String> = listOf(),
    val relatedCode: List<String> = listOf(),
    additionalRules: List<String> = listOf(),
    override val testType: TestCodeBuilderType,
) : TypedTestIns() {
    override fun unique(): Instruction {
        val input = StringBuilder()

        return Instruction(
            instruction = "Write unit test for following code.",
            input = "",
            output = "",
        )
    }
}