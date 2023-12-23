package cc.unitmesh.pick.strategy.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.strategy.base.TestStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

class ApiTestStrategyBuilder(private val context: JobContext) : TestStrategyBuilder {
    override fun build(): List<ApiTestIns> {
        TODO("Not yet implemented")
    }
}

class ApiTestIns(override val type: TestCodeBuilderType) : TypedTestIns {
    override fun unique(): Instruction {
        TODO("Not yet implemented")
    }

}
