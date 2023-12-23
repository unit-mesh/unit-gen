package cc.unitmesh.pick.strategy.unittest

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

/**
 *
 * correctly only support junit4, and Java
 */
class MethodTestStrategyBuilder(private val context: JobContext) : CodeStrategyBuilder {
    override fun build(): List<MethodTestIns> {
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

class MethodTestIns(override val type: CompletionBuilderType) : TypedIns {
    override fun unique(): Instruction {
        TODO("Not yet implemented")
    }

}
