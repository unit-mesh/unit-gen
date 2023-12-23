package cc.unitmesh.core.completion

import cc.unitmesh.core.Instruction

interface TypedIns {
    val type: CompletionBuilderType
    fun unique(): Instruction
}
