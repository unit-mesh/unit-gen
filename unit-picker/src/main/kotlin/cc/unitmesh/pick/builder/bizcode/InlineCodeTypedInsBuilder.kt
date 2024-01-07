package cc.unitmesh.pick.builder.bizcode

import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeFunction

class InlineCodeTypedInsBuilder(val context: JobContext) : TypedInsBuilder {
    private fun isValidTypeOver(char: Char): Boolean {
        return char == ')' || char == ']' || char == '}' || char == '"' || char == '\'' || char == '>' || char == ';' || char == ','
    }

    private val GOOD_FOR_SUGGEST_LENGTH = 10

    /**
     * Builds a list of code completion instructions by splitting the code lines and analyzing each character.
     * For example, given the input: `println("Hello, world!")`
     * The output will be a list of CodeCompletionIns instances representing different cursor positions:
     *
     * - `CodeCompletionIns(beforeCursor = "println(", afterCursor = "Hello, world!")`
     * - `CodeCompletionIns(beforeCursor = "println(\"Hello,", afterCursor = " world!\")"`
     * - `CodeCompletionIns(beforeCursor = "println(\"Hello, world!", afterCursor = "\")"`
     * - `CodeCompletionIns(beforeCursor = "println(\"Hello, world!\")", afterCursor = "")`
     *
     * @param function The CodeFunction object containing information about the code.
     * @return A list of CodeCompletionIns instances representing different cursor positions.
     */
    override fun build(function: CodeFunction): List<CodeCompletionIns> {
        val position = function.Position
        val functionCode: List<String> =
            context.job.codeLines.subList(position.StartLine, position.StopLine).joinToString("\n")
                .lines()

        val completions = mutableListOf<CodeCompletionIns>()

        functionCode.forEach { line ->
            for (i in line.indices) {
                if (isValidTypeOver(line[i])) {
                    val beforeCursor = line.substring(0, i + 1).trim()
                    val afterCursor = line.substring(i + 1).trimEnd()

                    if (beforeCursor.isBlank() || afterCursor.isBlank()) {
                        continue
                    }

                    // if afterCursor is a ';', skip it
                    if (afterCursor.length < GOOD_FOR_SUGGEST_LENGTH) {
                        continue
                    }

                    completions.add(CodeCompletionIns(beforeCursor, afterCursor, InstructionBuilderType.INLINE_COMPLETION))
                }
            }
        }

        return completions
    }
}
