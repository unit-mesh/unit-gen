package cc.unitmesh.pick.builder.comment.ins

import cc.unitmesh.core.Instruction
import cc.unitmesh.quality.comment.CodeComment
import cc.unitmesh.core.comment.CommentBuilderType
import cc.unitmesh.core.comment.DocCommentToolType
import cc.unitmesh.core.comment.TypedCommentIns
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.Serializable

@Serializable
data class ClassCommentIns(
    val docInstruction: DocCommentToolType,
    val dataStructure: CodeDataStruct,
    val comment: CodeComment,
    val language: String,
) : TypedCommentIns() {
    override val builderLevel: CommentBuilderType = CommentBuilderType.CLASS_LEVEL

    override fun toInstruction(): Instruction {
        val instruction = "Write ${docInstruction.value} for given class " + dataStructure.NodeName + " .\n"
        val input = "Code:\n```$language\n" + dataStructure.Content + "\n```"
        val output = comment.content

        return Instruction(instruction, input, output)
    }
}