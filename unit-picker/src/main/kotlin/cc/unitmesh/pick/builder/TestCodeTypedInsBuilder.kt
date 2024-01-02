package cc.unitmesh.pick.builder

import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.builder.unittest.base.UnitTestService
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

class TestCodeTypedInsBuilder(val context: JobContext) : TypedInsBuilder {
    override fun build(dataStruct: CodeDataStruct): List<TypedIns> {
        val testIns = UnitTestService.lookup(dataStruct, context).map {
            it.build(dataStruct)
        }.flatten()

        return testIns
    }

    override fun build(container: CodeContainer): List<TypedIns> {
        val testIns = UnitTestService.lookup(container, context).map {
            it.build(container)
        }.flatten()

        return testIns
    }

    override fun build(function: CodeFunction): List<CodeCompletionIns> {
        return listOf()
    }

}
