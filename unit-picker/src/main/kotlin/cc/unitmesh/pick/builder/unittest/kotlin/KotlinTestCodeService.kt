package cc.unitmesh.pick.builder.unittest.kotlin

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.builder.unittest.java.ClassTestCodeBuilder
import cc.unitmesh.pick.builder.unittest.java.JavaTestCodeService
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeDataStruct

/**
 * 为给定的 CodeDataStruct 的每个 CodeFunction 生成测试指令。
 */
class KotlinTestCodeService(override val context: JobContext) : JavaTestCodeService(context) {
    override fun isApplicable(dataStruct: CodeDataStruct): Boolean {
        return context.project.language == SupportedLang.KOTLIN && dataStruct.NodeName.endsWith("Test") || dataStruct.NodeName.endsWith("Tests")
    }

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