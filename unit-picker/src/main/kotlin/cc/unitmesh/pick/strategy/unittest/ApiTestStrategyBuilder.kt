package cc.unitmesh.pick.strategy.unittest

import cc.unitmesh.pick.prompt.ins.RelatedCodeCompletionIns
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

class ApiTestStrategyBuilder(private val context: JobContext) :
    CodeStrategyBuilder<RelatedCodeCompletionIns> {
    override fun build(): List<RelatedCodeCompletionIns> {
        TODO("Not yet implemented")
    }

}
