package cc.unitmesh.pick.builder.unittest.rust

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.builder.unittest.base.UnitTestService
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct

class RustTestCodeService(val job: JobContext) : UnitTestService {
    override fun isApplicable(dataStruct: CodeDataStruct): Boolean = false

    override fun isApplicable(container: CodeContainer): Boolean = job.project.language == SupportedLang.RUST

    override fun build(container: CodeContainer): List<TypedTestIns> {
        val testCode = container.DataStructures.filter { it.Module == "tests" }
        return emptyList()
    }

    override fun build(dataStruct: CodeDataStruct): List<TypedTestIns> {
        return emptyList()
    }
}