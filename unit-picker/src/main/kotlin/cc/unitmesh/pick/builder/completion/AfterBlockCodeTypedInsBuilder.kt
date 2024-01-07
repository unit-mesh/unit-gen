package cc.unitmesh.pick.builder.completion

import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeFunction


/**
 * The `AfterBlockCodeTypedInsBuilder` class is responsible for building a list of code completion instructions
 * after a block of code has been typed.
 *
 * @property context The job context associated with the code completion.
 */
class AfterBlockCodeTypedInsBuilder(val context: JobContext) : TypedInsBuilder {

    /**
     * Builds a list of code completion instructions based on the provided code function.
     *
     * @param function The code function for which the code completion instructions are to be built.
     * @return A list of code completion instructions.
     */
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

        return listOf(CodeCompletionIns(beforeCursor, afterCursor, InstructionBuilderType.AFTER_BLOCK_COMPLETION))
    }
}