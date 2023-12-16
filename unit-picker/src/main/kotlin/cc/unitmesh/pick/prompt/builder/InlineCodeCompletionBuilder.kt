package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.CodeCompletionIns
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.JobContext
import chapi.domain.core.CodeFunction

class InlineCodeCompletionBuilder(val function: CodeFunction) : InstructionBuilder {
    override fun build(context: JobContext): List<CodeCompletionIns> {
        TODO("Not yet implemented")
    }
}