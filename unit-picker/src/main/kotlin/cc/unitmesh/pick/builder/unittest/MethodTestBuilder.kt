package cc.unitmesh.pick.builder.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.strategy.base.TestBuilder
import cc.unitmesh.pick.worker.job.JobContext

class MethodTestBuilder(private val context: JobContext) : TestBuilder {
    override fun build(): List<MethodTestIns> {
        TODO("Not yet implemented")
    }

}

class MethodTestIns(override val type: TestCodeBuilderType) : TypedTestIns {
    override fun unique(): Instruction {
        TODO("Not yet implemented")
    }

}
