package cc.unitmesh.pick.strategy.bizcode

import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

/**
 * 对于其它不需要上下文的 AI 能力，需要实现一个空的上下文策略。
 */
class EmptyContextStrategyBuilder(context: JobContext) : CodeStrategyBuilder {
    override fun build(): List<TypedIns> {
        return emptyList()
    }

}
