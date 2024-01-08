package cc.unitmesh.pick.builder.comment

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.quality.comment.CodeComment

/**
 * Extracts the documentation comments (KDoc) from the given code.
 *
 * @param code the Kotlin code from which to extract the KDoc comments
 * @return a list of pairs, where each pair contains the line number and the extracted KDoc comment
 */
fun extractComments(code: String, language: SupportedLang): List<CodeComment> {
    return when (language) {
        SupportedLang.KOTLIN -> CodeComment.parseComment(code)
        SupportedLang.JAVA -> CodeComment.parseComment(code)
        else -> {
            println("Unsupported language: $language")
            emptyList()
        }
    }
}