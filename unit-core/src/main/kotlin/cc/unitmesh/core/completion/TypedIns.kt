package cc.unitmesh.core.completion

import cc.unitmesh.core.Instruction

interface TypedIns {
    val type: InstructionBuilderType
    fun unique(): Instruction
}
