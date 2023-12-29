package cc.unitmesh.pick.strategy.bizcode

import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

/**
 * 对于其它不需要上下文的 AI 能力，需要实现一个空的上下文策略，如注释生成。
 */
class CommentsStrategyBuilder(val context: JobContext) : CodeStrategyBuilder {
    override fun build(): List<TypedIns> {
        val language = context.job.fileSummary.language.lowercase()
        val container = context.job.container ?: return emptyList()

        return emptyList()
    }
}
