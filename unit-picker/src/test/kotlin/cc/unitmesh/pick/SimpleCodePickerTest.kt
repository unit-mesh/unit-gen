package cc.unitmesh.pick;

import cc.unitmesh.core.Instruction
import cc.unitmesh.pick.ext.GitUtil
import cc.unitmesh.pick.option.InsPickerOption
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class SimpleCodePickerTest {

    @Test
    fun shouldCheckoutTestCode() {
        val picker = SimpleCodePicker(
            InsPickerOption(
                url = "https://github.com/unit-mesh/unit-eval-testing",
                completionTypeSize  = 10,
                maxCharInCode = 100
            )
        )

        runBlocking {
            val output: MutableList<Instruction> = picker.execute()
            File("test.jsonl").writeText(output.joinToString("\n") {
                Json.encodeToString(it)
            })
        }
    }
}