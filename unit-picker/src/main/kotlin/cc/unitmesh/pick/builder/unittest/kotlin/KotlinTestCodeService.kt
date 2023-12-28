package cc.unitmesh.pick.builder.unittest.kotlin

import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.builder.unittest.java.ClassTestCodeBuilder
import cc.unitmesh.pick.builder.unittest.java.JavaMethodTestCodeBuilder
import cc.unitmesh.pick.builder.unittest.java.JavaTestCodeService
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

class KotlinTestCodeService(override val context: JobContext) : JavaTestCodeService(context) {
    override fun build(dataStruct: CodeDataStruct): List<TypedTestIns> {
        val underTestFile = this.findUnderTestFile(dataStruct).firstOrNull() ?: return emptyList()
        val relevantClasses = this.lookupRelevantClass(dataStruct)

        val classTestIns = ClassTestCodeBuilder(context)
            .build(dataStruct, underTestFile, relevantClasses)

        val methodTests = KotlinMethodTestCodeBuilder(context)
            .build(dataStruct, underTestFile, relevantClasses)

        return classTestIns + methodTests
    }
}