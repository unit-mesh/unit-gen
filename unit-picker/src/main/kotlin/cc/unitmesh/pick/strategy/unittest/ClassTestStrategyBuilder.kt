package cc.unitmesh.pick.strategy.unittest

import cc.unitmesh.pick.prompt.ins.RelatedCodeIns
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

class ClassTestStrategyBuilder(private val context: JobContext) : CodeStrategyBuilder {
    override fun build(): List<RelatedCodeIns> {
        TODO("Not yet implemented")
    }

}
