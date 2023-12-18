package cc.unitmesh.pick.prompt;

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InstructionTest {

    @Test
    fun `should_return_all_instructions_when_size_is_less_than_maxCompletionInOneFile`() {
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
    fun `should_return_first_instruction_when_maxCompletionInOneFile_is_1`() {
        // given
        val instructions = listOf(
            Instruction("instruction1", "input1", "output1"),
            Instruction("instruction2", "input2", "output2"),
            Instruction("instruction3", "input3", "output3")
        )
        val maxCompletionInOneFile = 1
        val expected = listOf(instructions[0])

        // when
        val result = Instruction.takeStrategy(instructions, maxCompletionInOneFile)

        // then
        assertEquals(expected, result)
    }

    @Test
    fun `should_return_first_maxCompletionInOneFile_instructions_and_random_rest_when_size_is_greater_than_maxCompletionInOneFile`() {
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
        assertEquals(maxCompletionInOneFile, 3)
        assertEquals(instructions[0], result[0])
        assertEquals(instructions[1], result[1])
        assertEquals(instructions[2], result[2])
        // Ensure that the remaining instructions are random
        for (i in maxCompletionInOneFile until instructions.size) {
            assert(instructions.contains(result[i]))
        }
    }
}
