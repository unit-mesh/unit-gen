package cc.unitmesh.pick.prompt.completion

import cc.unitmesh.pick.prompt.CodeCompletionIns
import cc.unitmesh.pick.prompt.CompletionBuilder
import cc.unitmesh.pick.prompt.JobContext
import chapi.domain.core.CodeFunction

class InlineCodeCompletionBuilder(val context: JobContext) : CompletionBuilder {
    fun isValidTypeOver(char: Char): Boolean {
        return char == ')' || char == ']' || char == '}' || char == '"' || char == '\'' || char == '>' || char == ';'
    }

    override fun build(function: CodeFunction): List<CodeCompletionIns> {
        TODO("Not yet implemented")
    }
}