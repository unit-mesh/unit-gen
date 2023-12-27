package cc.unitmesh.pick.builder.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BasicTestIns(
    val language: SupportedLang,
    val underTestCode: String,
    val generatedCode: String,
    val coreFrameworks: List<String> = listOf(),
    val testFrameworks: List<String> = listOf(),
    /**
     * the Specification of the test
     */
    val specs: List<String> = listOf(),
    private val relatedCode: List<String> = listOf(),
    override val testType: TestCodeBuilderType,
) : TypedTestIns() {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }

    override fun unique(): Instruction {
        val input = StringBuilder()

        input.append("\n###")
        input.append(specs.joinToString("\n") { "- $it" })
        input.append("\n")

        if (coreFrameworks.isNotEmpty()) {
            input.append("- You are working on a project that uses ${coreFrameworks.joinToString(", ")}\n")
        }

        if (testFrameworks.isNotEmpty()) {
            input.append("- This project uses ${testFrameworks.joinToString(", ")} to test code.\n")
        }

        if (relatedCode.isNotEmpty()) {
            input.append("Related code:\n")
            input.append(relatedCode.joinToString("\n"))
        }
        input.append("\n###")

        input.append("Code under test:\n")
        input.append("```${language.name.lowercase()}\n")
        input.append(underTestCode)
        input.append("\n```")

        when(testType) {
            TestCodeBuilderType.CLASS_UNIT -> {
                input.append("\nStart test code with `package` syntax here: \n")
            }
            TestCodeBuilderType.METHOD_UNIT -> {
                input.append("\nStart test code with `@Test` syntax here: \n")
            }
            else -> {}
        }

        return Instruction(
            instruction = """Write $language unit test for following code.""",
            input = input.toString(),
            output = generatedCode,
        )
    }
}