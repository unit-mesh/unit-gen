package cc.unitmesh.pick.builder.comment

import cc.unitmesh.core.comment.CommentBuilder
import cc.unitmesh.core.comment.DocInstruction
import cc.unitmesh.core.completion.TypedIns
import chapi.domain.core.CodeContainer

class KotlinCommentBuilder : CommentBuilder {
    override val commentStart: String = "/**"
    override val commentEnd: String = "*/"
    override val docInstruction: DocInstruction = DocInstruction.KOTLIN

    override fun build(container: CodeContainer): List<TypedIns> {
        TODO("Not yet implemented")
    }
}
