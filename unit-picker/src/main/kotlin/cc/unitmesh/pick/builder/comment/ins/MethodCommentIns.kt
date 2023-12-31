package cc.unitmesh.pick.builder.comment.ins

import cc.unitmesh.core.Instruction
import cc.unitmesh.quality.comment.CodeComment
import cc.unitmesh.core.comment.CommentBuilderType
import cc.unitmesh.core.comment.DocCommentToolType
import cc.unitmesh.core.comment.TypedCommentIns
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import kotlinx.serialization.Serializable

@Serializable
data class MethodCommentIns(
    val docInstruction: DocCommentToolType,
    val function: CodeFunction,
    val comment: CodeComment,
    val currentDataStruct: CodeDataStruct,
    val language: String,
) : TypedCommentIns() {
    override val builderLevel: CommentBuilderType = CommentBuilderType.METHOD_LEVEL

    override fun toInstruction(): Instruction {
        val instruction = "Write ${docInstruction.value} for given method " + function.Name + " .\n"
        val input =
            "### Current class:\n${currentDataStruct.toUml()}\n###\nCode:\n```$language\n${currentDataStruct.Content}\n```\n"
        val output = comment.content

        return Instruction(instruction, input, output)
    }
}