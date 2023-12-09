package cc.unitmesh.pick.config

import chapi.domain.core.CodeContainer
import kotlinx.serialization.Serializable
import org.archguard.scanner.analyser.count.FileJob

@Serializable
class InstructionJob(
    var fileSummary: FileJob,
    var code: String = "",
    var container: CodeContainer? = null,
    var codeLines: List<String> = listOf(),
) {
    companion object {
        fun from(fileJob: FileJob): InstructionJob {
            return InstructionJob(
                code = fileJob.content.decodeToString(),
                fileSummary = fileJob
            )
        }
    }
}
