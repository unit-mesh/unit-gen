package cc.unitmesh.pick.prompt.strategy

import cc.unitmesh.pick.prompt.CompletionBuilderType
import cc.unitmesh.pick.prompt.Instruction

interface TypedCompletion {
    val type: CompletionBuilderType
    fun unique(): Instruction
}
