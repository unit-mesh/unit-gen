package cc.unitmesh.pick.builder

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.SupportedLang.*
import cc.unitmesh.core.comment.DocInstruction
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.pick.builder.comment.JvmCommentBuilder
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeContainer

class DocumentationTypedInsBuilder(val context: JobContext) : TypedInsBuilder {
    private val kotlinCommentBuilder = JvmCommentBuilder("kotlin")
    private val javaCommentBuilder = JvmCommentBuilder("java", DocInstruction.JAVA)

    override fun build(container: CodeContainer): List<TypedIns> {
        val language = context.project.language
        return when (language) {
            JAVA -> javaCommentBuilder.build(context.job.code, container)
            KOTLIN -> {
                kotlinCommentBuilder.build(context.job.code, container)
            }

            TYPESCRIPT -> TODO()
            RUST -> TODO()
        }
    }
}
