package cc.unitmesh.core.completion

import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.Instruction

interface TypedCompletionIns {
    val type: CompletionBuilderType

    fun unique(): Instruction
}
