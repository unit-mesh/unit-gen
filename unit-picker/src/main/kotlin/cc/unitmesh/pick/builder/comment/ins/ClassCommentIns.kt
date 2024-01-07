package cc.unitmesh.pick.builder.comment.ins

import cc.unitmesh.core.Instruction
import cc.unitmesh.quality.documentation.CodeComment
import cc.unitmesh.core.comment.CommentBuilderType
import cc.unitmesh.core.comment.DocCommentInstruction
import cc.unitmesh.core.comment.TypedCommentIns
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.Serializable

@Serializable
data class ClassCommentIns(
    val docInstruction: DocCommentInstruction,
    val dataStructure: CodeDataStruct,
    val comment: CodeComment,
    val language: String,
) : TypedCommentIns() {
    override val builderLevel: CommentBuilderType = CommentBuilderType.CLASS_LEVEL

    override fun unique(): Instruction {
        val instruction = "Write ${docInstruction.value} for given class " + dataStructure.NodeName + " .\n"
        val input = "Code:\n```$language\n" + dataStructure.Content + "\n```"
        val output = comment.content

        return Instruction(instruction, input, output)
    }
}