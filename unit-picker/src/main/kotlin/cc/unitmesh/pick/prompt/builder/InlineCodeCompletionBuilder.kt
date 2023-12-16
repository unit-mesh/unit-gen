package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.CodeCompletionIns
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.JobContext
import chapi.domain.core.CodeFunction

class InlineCodeCompletionBuilder(val context: JobContext) : InstructionBuilder {
    fun isValidTypeOver(char: Char): Boolean {
        return char == ')' || char == ']' || char == '}' || char == '"' || char == '\'' || char == '>' || char == ';'
    }

    override fun build(function: CodeFunction): List<CodeCompletionIns> {
        TODO("Not yet implemented")
    }
}