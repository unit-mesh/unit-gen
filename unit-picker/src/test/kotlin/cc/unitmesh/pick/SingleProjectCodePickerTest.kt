package cc.unitmesh.pick

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.pick.option.InsPickerOption
import cc.unitmesh.pick.strategy.CodeStrategyType
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
                codeStrategyTypes = listOf(CodeStrategyType.RELATED_CODE),
                instructionTypes = listOf(
                    InstructionBuilderType.DOCUMENTATION, InstructionBuilderType.TEST_CODE_GEN
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

    @Test
    fun should_handle_for_rust_test_gen() {
        val picker = SingleProjectCodePicker(
            InsPickerOption(
                language = "rust",
                url = "https://github.com/unit-mesh/edge-infer",
                maxTokenLength = 8192,
                codeStrategyTypes = listOf(CodeStrategyType.RELATED_CODE),
                instructionTypes = listOf(
                    InstructionBuilderType.TEST_CODE_GEN
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
