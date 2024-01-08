package cc.unitmesh.pick.worker.job

import chapi.domain.core.CodeContainer
import kotlinx.serialization.Serializable
import org.archguard.scanner.analyser.count.FileJob
import java.util.regex.Pattern

typealias FileSummary = FileJob

@Serializable
class InstructionFileJob(
    var fileSummary: FileJob,
    var code: String = "",
    var container: CodeContainer? = null,
    var codeLines: List<String> = listOf(),
) {
    companion object {
        fun from(fileJob: FileJob, canRemoveComment: Boolean): InstructionFileJob {
            val contentString = fileJob.content.decodeToString()
            val content = if (canRemoveComment) {
                removeMultipleComments(fileJob.language, contentString)
            } else {
                contentString
            }

            return InstructionFileJob(
                code = content,
                fileSummary = fileJob
            )
        }

    }
}

val pattern: Pattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL)

/**
 * Removes multiple comments from the given code.
 *
 * @param language the programming language of the code (e.g., "java")
 * @param code     the code from which comments should be removed
 * @return the code without multiple comments
 */
fun removeMultipleComments(language: String, code: String): String {
    return when (language.lowercase()) {
        "java" -> {
            val matcher = pattern.matcher(code)
            return matcher.replaceAll("")
        }

        else -> {
            code
        }
    }
}