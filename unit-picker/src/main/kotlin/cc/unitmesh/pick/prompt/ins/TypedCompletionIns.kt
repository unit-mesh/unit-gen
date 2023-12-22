package cc.unitmesh.pick.prompt.ins

import cc.unitmesh.pick.prompt.base.CompletionBuilderType
import cc.unitmesh.pick.prompt.base.Instruction

interface TypedCompletionIns {
    val type: CompletionBuilderType

    fun unique(): Instruction
}
