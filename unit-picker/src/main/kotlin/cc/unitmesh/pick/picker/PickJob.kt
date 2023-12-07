package cc.unitmesh.pick.picker

import chapi.domain.core.CodeContainer
import kotlinx.serialization.Serializable
import org.archguard.scanner.analyser.count.FileJob

@Serializable
class PickJob(
    var fileSummary: FileJob,
    var code: String = "",
    var container: CodeContainer? = null,
) {
    companion object {
        fun from(fileJob: FileJob): PickJob {
            return PickJob(
                code = fileJob.content.decodeToString(),
                fileSummary = fileJob
            )
        }
    }
}
