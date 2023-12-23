package cc.unitmesh.pick.strategy.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.pick.strategy.base.TestStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

class ClassTestStrategyBuilder(private val context: JobContext) : TestStrategyBuilder {
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

class ClassTestIns(override val type: TestCodeBuilderType) : TypedTestIns {
    override fun unique(): Instruction {
        TODO("Not yet implemented")
    }
}