package cc.unitmesh.pick.builder.comment

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.comment.*
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodePosition
import kotlinx.serialization.Serializable

class KotlinCommentBuilder : CommentBuilder {
    override val commentStart: String = "/**"
    override val commentEnd: String = "*/"
    override val docInstruction: DocInstruction = DocInstruction.KOTLIN

    override fun build(container: CodeContainer): List<TypedCommentIns> {
        return listOf()
    }

    companion object {
        private val commentPattern = Regex("""\s+/\*\*([^*]|(\*+[^*/]))*\*+/""")

        /**
         * Extracts the Kotlin documentation comments (KDoc) from the given code.
         *
         * @param code the Kotlin code from which to extract the KDoc comments
         * @return a list of pairs, where each pair contains the line number and the extracted KDoc comment
         */
        fun extractKdocComments(code: String): List<CodeComment> {
            val matches = commentPattern.findAll(code)

            val comments = mutableListOf<CodeComment>()

            for (match in matches) {
                val commentContent = match.value.trimIndent()
                val startLine = code.substring(0, match.range.first).count { it == '\n' } + 1
                val stopLine = code.substring(0, match.range.last).count { it == '\n' } + 1
                val startLinePosition = match.range.first - code.lastIndexOf('\n', match.range.first) - 1
                val stopLinePosition = match.range.last - code.lastIndexOf('\n', match.range.last) - 1

                val position = CodePosition(startLine, startLinePosition, stopLine, stopLinePosition)
                val comment = CodeComment(commentContent, position)
                comments.add(comment)
            }

            return comments
        }
    }
}

@Serializable
data class ClassCommentIns(
    val dataStructure: CodeDataStruct,
    val comment: CodeComment,
) : TypedCommentIns() {
    override val builderLevel: CommentBuilderType = CommentBuilderType.CLASS_LEVEL

    override fun unique(): Instruction {
        val instruction = "Write documentation for given class " + dataStructure.NodeName + " ."
        val input = dataStructure.Content
        val output = comment.content

        return Instruction(instruction, input, output)
    }
}