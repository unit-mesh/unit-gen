package cc.unitmesh.pick

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.option.InsPickerOption
import cc.unitmesh.pick.strategy.BuildPlanType
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
    fun should_handle_for_kotlin_test_gen() {
        val root = File(".").canonicalPath
        val picker = SingleProjectCodePicker(
            InsPickerOption(
                language = "kotlin",
                url = root,
                maxTokenLength = 8192,
                buildPlan = listOf(BuildPlanType.RELATED_CODE),
                completionTypes = listOf(
                    CompletionBuilderType.DOCUMENTATION, CompletionBuilderType.TEST_CODE_GEN
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
