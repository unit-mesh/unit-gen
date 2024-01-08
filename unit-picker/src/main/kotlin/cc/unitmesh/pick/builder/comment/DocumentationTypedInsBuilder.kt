package cc.unitmesh.pick.builder.comment

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.comment.DocCommentToolType
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeContainer

class DocumentationTypedInsBuilder(val context: JobContext) : TypedInsBuilder {
    private val kotlinCommentBuilder = JvmCommentBuilder(SupportedLang.KOTLIN)
    private val javaCommentBuilder = JvmCommentBuilder(SupportedLang.JAVA, DocCommentToolType.JAVA)

    override fun build(container: CodeContainer): List<TypedIns> {
        val language = context.project.language
        return when (language) {
            SupportedLang.JAVA -> javaCommentBuilder.build(context.job.code, container)
            SupportedLang.KOTLIN -> {
                kotlinCommentBuilder.build(context.job.code, container)
            }

            SupportedLang.TYPESCRIPT -> TODO()
            SupportedLang.RUST -> TODO()
        }
    }
}
