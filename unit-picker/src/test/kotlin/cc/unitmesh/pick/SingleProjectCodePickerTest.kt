package cc.unitmesh.pick;

import cc.unitmesh.core.Instruction
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
                completionTypeSize  = 10,
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
}