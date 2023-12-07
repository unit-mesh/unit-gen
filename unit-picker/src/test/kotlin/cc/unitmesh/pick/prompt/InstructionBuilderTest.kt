package cc.unitmesh.pick.prompt;


import cc.unitmesh.pick.prompt.builder.InlineCodeCompletionBuilder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class InstructionBuilderTest {
    @Test
    fun shouldConvertInstructionToJson() {
        val instruction = InlineCodeCompletionBuilder(
            "Inline Code Completion",
            "Input for inline code completion",
            "java",
            "import java.util.*;\n" +
                    "\n" +
                    "public class Main {\n" +
                    "    public static void main(String[] args) {\n"
        )

        val output = Json.encodeToString(instruction)
        println(output)
    }
}