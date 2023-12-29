package cc.unitmesh.pick.strategy.bizcode

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.builder.comment.KotlinCommentBuilder
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext

/**
 * 对于其它不需要上下文的 AI 能力，需要实现一个空的上下文策略，如注释生成。
 */
class CommentsStrategyBuilder(val context: JobContext) : CodeStrategyBuilder {
    private val kotlinCommentBuilder = KotlinCommentBuilder()

    override fun build(): List<TypedIns> {
        val container = context.job.container ?: return emptyList()

        val language = context.project.language
        return when (language) {
            SupportedLang.JAVA -> kotlinCommentBuilder.build(context.job.code, container)
            SupportedLang.TYPESCRIPT -> TODO()
            SupportedLang.KOTLIN -> {
                kotlinCommentBuilder.build(context.job.code, container)
            }
        }
    }
}
