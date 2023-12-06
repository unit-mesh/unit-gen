package cc.unitmesh.pick.picker

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.archguard.scanner.analyser.count.FileJob
import java.security.MessageDigest

@Serializable
class PickJob(
    var language: String = "",
    var possibleLanguages: List<String> = listOf(),
    var filename: String = "",
    var extension: String = "",
    var location: String = "",
    var symlocation: String = "",
    var content: ByteArray = byteArrayOf(),
    var bytes: Long = 0,
    var lines: Long = 0,
    var code: Long = 0,
    var comment: Long = 0,
    var blank: Long = 0,
    var complexity: Long = 0,
    var weightedComplexity: Double = 0.0,
    // skip serialisation
    @Transient
    var hash: MessageDigest = MessageDigest.getInstance("SHA-256"),
    var binary: Boolean = false,
    var minified: Boolean = false,
    var generated: Boolean = false,
    var endPoint: Int = 0,
) {
    companion object {
        fun from(fileJob: FileJob): PickJob {
            return PickJob(
                language = fileJob.language,
                possibleLanguages = fileJob.possibleLanguages,
                filename = fileJob.filename,
                extension = fileJob.extension,
                location = fileJob.location,
                symlocation = fileJob.symlocation,
                content = fileJob.content,
                bytes = fileJob.bytes,
                lines = fileJob.lines,
                code = fileJob.code,
                comment = fileJob.comment,
                blank = fileJob.blank,
                complexity = fileJob.complexity,
                weightedComplexity = fileJob.weightedComplexity,
                hash = fileJob.hash,
                binary = fileJob.binary,
                minified = fileJob.minified,
                generated = fileJob.generated,
                endPoint = fileJob.endPoint,
            )
        }
    }
}
