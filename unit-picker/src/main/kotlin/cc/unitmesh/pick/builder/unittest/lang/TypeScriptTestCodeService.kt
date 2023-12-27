package cc.unitmesh.pick.builder.unittest.lang

import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

class TypeScriptTestCodeService(job: JobContext): UnitTestService {
    override fun isApplicable(dataStruct: CodeDataStruct): Boolean {
        return false
    }

    override fun build(dataStruct: CodeDataStruct): List<TypedTestIns> {
        return emptyList()
    }
}