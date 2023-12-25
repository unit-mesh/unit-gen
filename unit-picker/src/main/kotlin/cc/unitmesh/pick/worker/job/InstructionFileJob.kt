package cc.unitmesh.pick.worker.job

import chapi.domain.core.CodeContainer
import kotlinx.serialization.Serializable
import org.archguard.scanner.analyser.count.FileJob
import java.util.regex.Pattern

@Serializable
class InstructionFileJob(
    var fileSummary: FileJob,
    var code: String = "",
    var container: CodeContainer? = null,
    var codeLines: List<String> = listOf(),
) {
    companion object {
        fun from(fileJob: FileJob): InstructionFileJob {
            val content = removeMultipleComments(fileJob.language, fileJob.content.decodeToString())
            // todo : remove comments
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

            // 用空字符串替换注释
            return matcher.replaceAll("")
        }

        else -> {
            code
        }
    }
}