package cc.unitmesh.pick.prompt.completion

import cc.unitmesh.pick.prompt.CodeCompletionIns
import cc.unitmesh.pick.prompt.CompletionBuilder
import cc.unitmesh.pick.prompt.JobContext
import chapi.domain.core.CodeFunction

class InlineCodeCompletionBuilder(val context: JobContext) : CompletionBuilder {
    private fun isValidTypeOver(char: Char): Boolean {
        return char == ')' || char == ']' || char == '}' || char == '"' || char == '\'' || char == '>' || char == ';' || char == ','
    }

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

                    completions.add(CodeCompletionIns(beforeCursor, afterCursor))
                }
            }
        }

        return completions
    }
}
