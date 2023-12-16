package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.CodeCompletionIns
import cc.unitmesh.pick.prompt.CompletionBuilder
import cc.unitmesh.pick.prompt.JobContext
import chapi.domain.core.CodeFunction

class InBlockCodeCompletionBuilder(val context: JobContext) : CompletionBuilder {
    override fun build(function: CodeFunction): List<CodeCompletionIns> {
        val position = function.Position
        val beforeCursor = context.job.codeLines.subList(0, position.StartLine).joinToString("\n")

        val stopLine = if (position.StopLine == 0) {
            context.job.codeLines.size
        } else {
            position.StopLine
        }

        val afterCursor = context.job.codeLines.subList(position.StartLine, stopLine).joinToString("\n")

        return listOf(CodeCompletionIns(beforeCursor, afterCursor))
    }
}