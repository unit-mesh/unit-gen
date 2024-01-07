package cc.unitmesh.core.comment

import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.core.completion.TypedIns

abstract class TypedCommentIns : TypedIns {
    override val type: InstructionBuilderType = InstructionBuilderType.DOCUMENTATION
    abstract val builderLevel: CommentBuilderType
}
