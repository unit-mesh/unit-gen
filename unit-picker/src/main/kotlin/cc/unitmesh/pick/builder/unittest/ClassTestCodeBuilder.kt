package cc.unitmesh.pick.builder.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.pick.worker.job.JobContext

class ClassTestCodeBuilder(private val context: JobContext) : TestCodeBuilder {
    override fun build(): List<ClassTestIns> {
        val dataStructures = context.job.container?.DataStructures
        val isTestFile = dataStructures?.any {
            it.NodeName.endsWith("Test") || it.NodeName.endsWith("Tests")
        }

        if (!isTestFile!!) {
            return emptyList()
        }



        return emptyList()
    }

}

class ClassTestIns(override val testType: TestCodeBuilderType) : TypedTestIns() {
    override fun unique(): Instruction {
        TODO("Not yet implemented")
    }
}