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
        val isTestFile = context.job.container?.DataStructures?.any {
            it.NodeName.endsWith("Test") || it.NodeName.endsWith("Tests")
        }

        if (!isTestFile!!) {
            return emptyList()
        }


        return emptyList()
    }

}
