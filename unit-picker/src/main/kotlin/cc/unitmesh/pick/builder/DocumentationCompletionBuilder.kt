package cc.unitmesh.pick.builder

import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.CompletionBuilder
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

class DocumentationCompletionBuilder(val context: JobContext) : CompletionBuilder {
    override fun build(dataStruct: CodeDataStruct): List<TypedIns> {
        return listOf()
    }

    override fun build(function: CodeFunction): List<CodeCompletionIns> {
        return listOf()
    }
}
