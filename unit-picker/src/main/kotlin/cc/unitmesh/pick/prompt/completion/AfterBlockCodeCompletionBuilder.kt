package cc.unitmesh.pick.prompt.completion

import cc.unitmesh.pick.prompt.CodeCompletionIns
import cc.unitmesh.pick.prompt.CompletionBuilder
import cc.unitmesh.pick.prompt.CompletionBuilderType
import cc.unitmesh.pick.prompt.JobContext
import chapi.domain.core.CodeFunction


class AfterBlockCodeCompletionBuilder(val context: JobContext) : CompletionBuilder {
    override fun build(function: CodeFunction): List<CodeCompletionIns> {
        val position = function.Position
        val codeLines = context.job.codeLines
        val beforeCursor = codeLines.subList(0, position.StopLine).joinToString("\n")
        var afterCursor = codeLines.subList(position.StopLine, codeLines.size).joinToString("\n")

        if (position.StopLine == 0) {
            afterCursor = codeLines.joinToString("\n")
        }

        // create line break by os
        val lineBreak = System.lineSeparator()
        if (afterCursor == "}$lineBreak") {
            return emptyList()
        }

        return listOf(CodeCompletionIns(beforeCursor, afterCursor, CompletionBuilderType.AFTER_BLOCK_COMPLETION))
    }
}