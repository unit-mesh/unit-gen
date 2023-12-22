package cc.unitmesh.pick.prompt.ins.base

import cc.unitmesh.pick.prompt.completion.CompletionBuilderType
import cc.unitmesh.pick.prompt.Instruction

interface TypedCompletionIns {
    val type: CompletionBuilderType

    fun unique(): Instruction
}
