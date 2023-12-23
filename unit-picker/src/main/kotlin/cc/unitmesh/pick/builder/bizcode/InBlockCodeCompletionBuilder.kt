package cc.unitmesh.pick.builder.bizcode

import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.CompletionBuilder
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.worker.job.JobContext
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

        return listOf(CodeCompletionIns(beforeCursor, afterCursor, CompletionBuilderType.IN_BLOCK_COMPLETION))
    }
}