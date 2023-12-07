package cc.unitmesh.pick.picker

import chapi.domain.core.CodeContainer
import kotlinx.serialization.Serializable
import org.archguard.scanner.analyser.count.FileJob

@Serializable
class PickJob(
    var fileSummary: FileJob,
    var container: CodeContainer? = null,
) {
    companion object {
        fun from(fileJob: FileJob): PickJob {
            return PickJob(
                fileSummary = fileJob
            )
        }
    }
}
