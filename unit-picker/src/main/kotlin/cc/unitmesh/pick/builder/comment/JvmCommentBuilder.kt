package cc.unitmesh.pick.builder.comment

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.quality.documentation.CodeComment
import cc.unitmesh.core.comment.CommentBuilder
import cc.unitmesh.core.comment.DocCommentInstruction
import cc.unitmesh.core.comment.TypedCommentIns
import cc.unitmesh.pick.builder.comment.ins.ClassCommentIns
import cc.unitmesh.pick.builder.comment.ins.MethodCommentIns
import chapi.domain.core.CodeContainer

private const val DOC_THRESHOLD = 5

class JvmCommentBuilder(val language: SupportedLang, override val docCommentInstruction: DocCommentInstruction = DocCommentInstruction.KOTLIN) :
    CommentBuilder {

    /**
     * Builds a list of TypedCommentIns objects based on the provided code and CodeContainer.
     *
     * @param code The code from which to extract comments.
     * @param container The CodeContainer containing the data structures and functions.
     * @return A list of TypedCommentIns objects representing the extracted comments.
     */
    override fun build(code: String, container: CodeContainer): List<TypedCommentIns> {
        val posComments = try {
            extractComments(code, language)
        } catch (e: Exception) {
            emptyList()
        }

        if (posComments.isEmpty()) return emptyList()

        val startLineCommentMap: Map<Int, CodeComment> =
            posComments.filter { it.content.isNotBlank() && it.content.length >= DOC_THRESHOLD }.associateBy {
                it.position.StopLine
            }

        val comments = mutableListOf<TypedCommentIns>()

        container.DataStructures.forEach { dataStruct ->
            val classComment = startLineCommentMap[dataStruct.Position.StartLine - 1]
            classComment?.let { comments.add(ClassCommentIns(docCommentInstruction, dataStruct, it, language = language.name)) }

            val methodCommentIns =
                dataStruct.Functions.filter { it.Name != "constructor" && it.Name != "PrimaryConstructor" }
                    .map { function ->
                        val functionComment = startLineCommentMap[function.Position.StartLine - 1] ?: return@map null
                        MethodCommentIns(docCommentInstruction, function, functionComment, dataStruct, language = language.name)
                    }

            comments.addAll(methodCommentIns.filterNotNull())
        }

        return comments
    }
}

