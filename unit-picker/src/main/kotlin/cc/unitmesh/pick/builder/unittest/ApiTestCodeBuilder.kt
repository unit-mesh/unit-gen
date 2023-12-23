package cc.unitmesh.pick.builder.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

class ApiTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {
    override fun build(dataStruct: CodeDataStruct, underTestFile: CodeDataStruct, relevantClasses: List<CodeDataStruct>): List<ApiTestIns> {
        TODO("Not yet implemented")
    }
}

class ApiTestIns(override val testType: TestCodeBuilderType) : TypedTestIns() {
    override fun unique(): Instruction {
        TODO("Not yet implemented")
    }

}
