package cc.unitmesh.pick.prompt.strategy

import cc.unitmesh.pick.prompt.CompletionBuilderType
import cc.unitmesh.pick.prompt.Instruction

interface TypedCompletionIns {
    val type: CompletionBuilderType

    fun unique(): Instruction
}
