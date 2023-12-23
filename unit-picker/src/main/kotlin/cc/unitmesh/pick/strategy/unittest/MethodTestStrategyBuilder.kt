package cc.unitmesh.pick.strategy.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.strategy.base.TestStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

class MethodTestStrategyBuilder(private val context: JobContext) : TestStrategyBuilder {
    override fun build(): List<MethodTestIns> {
        TODO("Not yet implemented")
    }

}

class MethodTestIns(override val type: TestCodeBuilderType) : TypedTestIns {
    override fun unique(): Instruction {
        TODO("Not yet implemented")
    }

}
