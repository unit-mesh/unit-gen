package cc.unitmesh.pick.builder

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.pick.builder.comment.KotlinCommentBuilder
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeContainer

class DocumentationTypedInsBuilder(val context: JobContext) : TypedInsBuilder {
    private val kotlinCommentBuilder = KotlinCommentBuilder()

    override fun build(container: CodeContainer): List<TypedIns> {
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
