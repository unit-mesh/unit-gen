package cc.unitmesh.pick.builder.comment

import cc.unitmesh.core.comment.CodeComment
import cc.unitmesh.core.comment.CommentBuilder
import cc.unitmesh.core.comment.DocInstruction
import cc.unitmesh.core.comment.TypedCommentIns
import cc.unitmesh.pick.builder.comment.ins.ClassCommentIns
import cc.unitmesh.pick.builder.comment.ins.MethodCommentIns
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodePosition

private const val DOC_THRESHOLD = 5

class JvmCommentBuilder(val language: String, override val docInstruction: DocInstruction = DocInstruction.KOTLIN) :
    CommentBuilder {

    override fun build(code: String, container: CodeContainer): List<TypedCommentIns> {
        val posComments = try {
            extractKdocComments(code)
        } catch (e: Exception) {
            emptyList()
        }

        val startLineCommentMap: Map<Int, CodeComment> =
            posComments.filter { it.content.isNotBlank() && it.content.length >= DOC_THRESHOLD }.associateBy {
                    it.position.StopLine
                }

        val comments = mutableListOf<TypedCommentIns>()

        container.DataStructures.forEach { dataStruct ->
            val classComment = startLineCommentMap[dataStruct.Position.StartLine - 1]
            classComment?.let { comments.add(ClassCommentIns(docInstruction, dataStruct, it, language = language)) }

            val methodCommentIns =
                dataStruct.Functions.filter { it.Name != "constructor" && it.Name != "PrimaryConstructor" }
                    .map { function ->
                        val functionComment = startLineCommentMap[function.Position.StartLine - 1] ?: return@map null
                        MethodCommentIns(docInstruction, function, functionComment, dataStruct, language = language)
                    }

            comments.addAll(methodCommentIns.filterNotNull())
        }

        return comments
    }

    /**
     * Extracts the Kotlin documentation comments (KDoc) from the given code.
     *
     * @param code the Kotlin code from which to extract the KDoc comments
     * @return a list of pairs, where each pair contains the line number and the extracted KDoc comment
     */
    fun extractKdocComments(code: String): List<CodeComment> {
        val pattern = Regex("""/\*\*[^*]*\*+([^/*][^*]*\*+)*/""")

        val matches = pattern.findAll(code)

        return matches.map { match ->
            val commentContent = match.value.trimIndent()
            val startLine = code.substring(0, match.range.first).count { it == '\n' } + 1
            val stopLine = code.substring(0, match.range.last).count { it == '\n' } + 1
            val startLinePosition = match.range.first - code.lastIndexOf('\n', match.range.first) - 1
            val stopLinePosition = match.range.last - code.lastIndexOf('\n', match.range.last) - 1

            val position = CodePosition(startLine, startLinePosition, stopLine, stopLinePosition)
            val content = reIndentComment(commentContent)

            CodeComment(content, position)
        }.toList()
    }

    /// Re-indent the comment to remove the leading spaces.
    private fun reIndentComment(content: String): String {
        val lines = content.split("\n")
        val indent = lines[1].takeWhile { it == ' ' }
        val linesWithoutIndent = lines.map { it.removePrefix(indent) }

        // except the first line, every line should have one leading space
        val linesWithLeadingSpace = linesWithoutIndent.mapIndexed { index, line ->
                if (index == 0) {
                    line
                } else {
                    " $line"
                }
            }

        return linesWithLeadingSpace.joinToString("\n")
    }
}
