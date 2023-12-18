package cc.unitmesh.pick.prompt;

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
        assertEquals(listOf(instructions.first()), result)
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
        assertEquals(instructions[0], result[0])
    }
}

