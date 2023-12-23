package cc.unitmesh.pick.builder.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.pick.worker.job.JobContext

class ApiTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {
    override fun build(): List<ApiTestIns> {
        TODO("Not yet implemented")
    }
}

class ApiTestIns(override val type: TestCodeBuilderType) : TypedTestIns {
    override fun unique(): Instruction {
        TODO("Not yet implemented")
    }

}
