package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.CodeCompletionIns
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.JobContext
import chapi.domain.core.CodeFunction

class InlineCodeCompletionBuilder(val context: JobContext) : InstructionBuilder {
    override fun buildFun(function: CodeFunction): List<CodeCompletionIns> {
        TODO("Not yet implemented")
    }

}