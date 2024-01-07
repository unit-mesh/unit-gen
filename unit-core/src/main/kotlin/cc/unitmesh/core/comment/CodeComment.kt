package cc.unitmesh.core.comment

import cc.unitmesh.core.SupportedLang
import chapi.domain.core.CodePosition
import kotlinx.serialization.Serializable

@Serializable
data class CodeComment(
    val content: String,
    val position: CodePosition
) {
    companion object {
        /**
         * Re-indent the comment to remove the leading spaces.
         *
         * @param content the comment content to be re-indented
         * @return the re-indented comment content
         */
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

        /**
         * Extracts the Kotlin documentation comments (KDoc) from the given code.
         *
         * @param code the Kotlin code from which to extract the KDoc comments
         * @return a list of pairs, where each pair contains the line number and the extracted KDoc comment
         */
        fun extractKdocComments(code: String, language: SupportedLang): List<CodeComment> {
            val pattern = Regex("""/\*\*[^*]*\*+([^/*][^*]*\*+)*/""")

            val matches = pattern.findAll(code)

            return matches.map { match ->
                val commentContent = match.value.trimIndent()
                val startLine = code.substring(0, match.range.first).count { it == '\n' } + 1
                val stopLine = code.substring(0, match.range.last).count { it == '\n' } + 1
                val startLinePosition = match.range.first - code.lastIndexOf('\n', match.range.first) - 1
                val stopLinePosition = match.range.last - code.lastIndexOf('\n', match.range.last) - 1

                val position = CodePosition(startLine, startLinePosition, stopLine, stopLinePosition)
                val content = Companion.reIndentComment(commentContent)

                CodeComment(content, position)
            }.toList()
        }
    }
}