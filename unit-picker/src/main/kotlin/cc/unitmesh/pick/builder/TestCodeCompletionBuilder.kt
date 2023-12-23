package cc.unitmesh.pick.builder

import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.CompletionBuilder
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.builder.unittest.lang.UnitTestService
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

class TestCodeCompletionBuilder(val context: JobContext) : CompletionBuilder {
    override fun build(dataStruct: CodeDataStruct): List<TypedIns> {
        val testIns = UnitTestService.lookup(dataStruct, context).map {
            it.build(dataStruct)
        }.flatten()

        return testIns
    }

    override fun build(function: CodeFunction): List<CodeCompletionIns> {
        return listOf()
    }

}
