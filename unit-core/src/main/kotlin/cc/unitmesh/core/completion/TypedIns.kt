package cc.unitmesh.core.completion

import cc.unitmesh.core.Instruction

interface TypedIns {
    val type: InstructionBuilderType

    /**
     * Build final instruction.
     */
    fun toInstruction(): Instruction
}
