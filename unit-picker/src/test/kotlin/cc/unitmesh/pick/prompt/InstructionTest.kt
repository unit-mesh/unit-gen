package cc.unitmesh.pick.prompt;

import cc.unitmesh.pick.prompt.base.Instruction
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class InstructionTest {

    @Test
    fun should_return_all_instructions_when_size_is_less_than_max_completion() {
        // given
        val instructions = listOf(
            Instruction("instruction1", "input1", "output1"),
            Instruction("instruction2", "input2", "output2"),
            Instruction("instruction3", "input3", "output3")
        )
        val maxCompletionInOneFile = 5

        // when
        val result = Instruction.takeStrategy(instructions, maxCompletionInOneFile)

        // then
        assertEquals(instructions, result)
    }

    @Test
    fun should_return_first_instruction_when_max_completion_is_1() {
        // given
        val instructions = listOf(
            Instruction("instruction1", "input1", "output1"),
            Instruction("instruction2", "input2", "output2"),
            Instruction("instruction3", "input3", "output3")
        )
        val maxCompletionInOneFile = 1

        // when
        val result = Instruction.takeStrategy(instructions, maxCompletionInOneFile)

        // then
        assertEquals(listOf(instructions.last()), result)
    }

    @Test
    fun should_return_first_maxCompletion_instructions_and_random_rest_when_size_is_more_than_maxCompletion() {
        // given
        val instructions = listOf(
            Instruction("instruction1", "input1", "output1"),
            Instruction("instruction2", "input2", "output2"),
            Instruction("instruction3", "input3", "output3"),
            Instruction("instruction4", "input4", "output4"),
            Instruction("instruction5", "input5", "output5")
        )
        val maxCompletionInOneFile = 3

        // when
        val result = Instruction.takeStrategy(instructions, maxCompletionInOneFile)

        // then
        assertEquals(3, result.size)
        assertEquals(instructions.last(), result.first())
    }


    @Test
    fun should_return_not_pretty_json_string_when_pretty_is_false_and_mergeInput_is_false() {
        // Given
        val instruction = Instruction(
            "instruction",
            "input",
            "output"
        )

        // When
        val result = instruction.render(pretty = false, mergeInput = false)

        // Then
        result shouldBe "{\"instruction\":\"instruction\",\"input\":\"input\",\"output\":\"output\"}"
    }

    @Test
    fun should_return_pretty_json_string_when_pretty_is_true_and_mergeInput_is_false() {
        // Given
        val instruction = Instruction(
            "instruction",
            "input",
            "output"
        )

        // When
        val result = instruction.render(pretty = true, mergeInput = false)

        // Then
        assertThat(result).isEqualTo(
            """
            {
                "instruction": "instruction",
                "input": "input",
                "output": "output"
            }
            """.trimIndent()
        )
    }

    @Test
    fun should_return_not_pretty_json_string_with_merged_input_when_pretty_is_false_and_mergeInput_is_true() {
        // Given
        val instruction = Instruction(
            "instruction",
            "input",
            "output"
        )

        // When
        val result = instruction.render(pretty = false, mergeInput = true)

        // Then
        assertThat(result).isEqualTo("{\"instruction\":\"instruction\\ninput\",\"output\":\"output\"}")
    }
}