package cc.unitmesh.pick.builder.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.SupportedLang
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
                lang = context.project.language,
                underTestCode = underTestFile.Content,
                generatedCode = dataStruct.Content,
                coreFrameworks = context.project.coreFrameworks,
                testFrameworks = context.project.testFrameworks,
                testType = TestCodeBuilderType.CLASS_UNIT,
                specs = listOf(
                    "You MUST use should_xx_xx style for test method name.",
                    "You MUST use given-when-then style.",
                    "Test file should be complete and compilable, without need for further actions.",
                    "Instead of using `@BeforeEach` methods for setup, include all necessary code initialization within each individual test method, do not write parameterized tests."
                )
            )
        )
    }

}

class ClassTestIns(
    val lang: SupportedLang,
    val underTestCode: String,
    val generatedCode: String,
    val coreFrameworks: List<String> = listOf(),
    val testFrameworks: List<String> = listOf(),
    /**
     * the Specification of the test
     */
    val specs: List<String> = listOf(),
    val relatedCode: List<String> = listOf(),
    override val testType: TestCodeBuilderType,
) : TypedTestIns() {
    override fun unique(): Instruction {
        val input = StringBuilder()

        input.append(specs.joinToString("\n"))

        if (coreFrameworks.isNotEmpty()) {
            input.append("Core frameworks: ${coreFrameworks.joinToString(", ")}\n")
        }

        if (testFrameworks.isNotEmpty()) {
            input.append("Test frameworks: ${testFrameworks.joinToString(", ")}\n")
        }

        if (coreFrameworks.contains("Spring Boot")) {
            input.append("Use appropriate Spring test annotations such as `@MockBean`, `@Autowired`, `@WebMvcTest`, `@DataJpaTest`, `@AutoConfigureTestDatabase`, `@AutoConfigureMockMvc`, `@SpringBootTest` etc.")
        }

        if (relatedCode.isNotEmpty()) {
            input.append("Related code:\n")
            input.append(relatedCode.joinToString("\n"))
        }

        input.append("Code under test:\n")
        input.append("```${lang.name.lowercase()}\n")
        input.append(underTestCode)
        input.append("\n```")

        return Instruction(
            instruction = "Write unit test for following code.",
            input = input.toString(),
            output = generatedCode,
        )
    }
}