package cc.unitmesh.pick;

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.option.InsPickerOption
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.File

class SingleProjectCodePickerTest {

    @Test
    fun shouldCheckoutTestCode() {
        val picker = SingleProjectCodePicker(
            InsPickerOption(
                url = "https://github.com/unit-mesh/unit-eval-testing",
                completionTypeSize = 10,
                maxCharInCode = 100,
            )
        )

        val outputFile = File("test.jsonl")
        runBlocking {
            val output: MutableList<Instruction> = picker.execute()
            outputFile.writeText(output.joinToString("\n") {
                Json.encodeToString(it)
            })
        }
    }

    @Test
    fun shouldCheckoutTestCodeWithBadSmell() {
        val picker = SingleProjectCodePicker(
            InsPickerOption(
                language = "kotlin",
                url = "https://github.com/unit-mesh/unit-eval",
                completionTypeSize = 10,
                maxCharInCode = 100,
                completionTypes = listOf(
                    CompletionBuilderType.IN_BLOCK_COMPLETION,
//                    CompletionBuilderType.TEST_CODE_GEN, CompletionBuilderType.DOCUMENTATION
                ),
            )
        )

        val outputFile = File("test.jsonl")
        runBlocking {
            val output: MutableList<Instruction> = picker.execute()
            outputFile.writeText(output.joinToString("\n") {
                Json.encodeToString(it)
            })
        }
    }
}