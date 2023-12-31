package cc.unitmesh.pick.builder.completion

import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeFunction

class InBlockCodeTypedInsBuilder(val context: JobContext) : TypedInsBuilder {
    override fun build(function: CodeFunction): List<CodeCompletionIns> {
        val position = function.Position
        val beforeCursor = context.job.codeLines.subList(0, position.StartLine).joinToString("\n")

        val stopLine = if (position.StopLine == 0) {
            context.job.codeLines.size
        } else {
            position.StopLine
        }

        val afterCursor = context.job.codeLines.subList(position.StartLine, stopLine).joinToString("\n")

        return listOf(CodeCompletionIns(beforeCursor, afterCursor, InstructionBuilderType.IN_BLOCK_COMPLETION))
    }
}