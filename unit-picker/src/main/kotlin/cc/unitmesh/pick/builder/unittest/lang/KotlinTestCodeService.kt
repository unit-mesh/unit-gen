package cc.unitmesh.pick.builder.unittest.lang

import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.builder.unittest.ClassTestCodeBuilder
import cc.unitmesh.pick.builder.unittest.MethodTestCodeBuilder
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

class KotlinTestCodeService(override val context: JobContext) : JavaTestCodeService(context) {
    override fun build(dataStruct: CodeDataStruct): List<TypedTestIns> {
        val underTestFile = this.findUnderTestFile(dataStruct).firstOrNull() ?: return emptyList()
        val relevantClasses = this.lookupRelevantClass(dataStruct)

        val classTestIns = ClassTestCodeBuilder(context)
            .build(dataStruct, underTestFile, relevantClasses)

        val methodTests = MethodTestCodeBuilder(context)
            .build(dataStruct, underTestFile, relevantClasses)

        return classTestIns + methodTests
    }
}