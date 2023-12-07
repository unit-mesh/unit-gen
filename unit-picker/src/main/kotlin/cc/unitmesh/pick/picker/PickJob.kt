package cc.unitmesh.pick.picker

import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.archguard.scanner.analyser.count.FileJob
import java.security.MessageDigest

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
