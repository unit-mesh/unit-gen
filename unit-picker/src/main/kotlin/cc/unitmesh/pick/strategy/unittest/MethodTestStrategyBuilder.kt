package cc.unitmesh.pick.strategy.unittest

import cc.unitmesh.pick.prompt.ins.RelatedCodeCompletionIns
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

/**
 *
 * correctly only support junit4, and Java
 */
class MethodTestStrategyBuilder(private val context: JobContext) :
    CodeStrategyBuilder<RelatedCodeCompletionIns> {
    override fun build(): List<RelatedCodeCompletionIns> {
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
