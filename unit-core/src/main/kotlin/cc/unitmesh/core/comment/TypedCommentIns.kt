package cc.unitmesh.core.comment

import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.completion.TypedIns

abstract class TypedCommentIns : TypedIns {
    override val type: CompletionBuilderType = CompletionBuilderType.DOCUMENTATION
    abstract val builderLevel: CommentBuilderType
}
