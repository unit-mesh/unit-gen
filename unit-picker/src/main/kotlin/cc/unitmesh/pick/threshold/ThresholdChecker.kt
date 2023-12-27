package cc.unitmesh.pick.threshold

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.job.InstructionFileJob
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.EncodingRegistry
import com.knuddels.jtokkit.api.EncodingType
import org.archguard.scanner.analyser.count.LanguageService
import org.slf4j.Logger

/**
 * The `ThresholdChecker` class is responsible for determining whether a given job or instruction
 * meets the threshold criteria for processing. It utilizes various criteria, including file type,
 * code complexity, file size, and token length, to make these determinations.
 *
 * @property context The worker context providing configuration settings for the threshold checks.
 */
class ThresholdChecker(private val context: WorkerContext) {
    private var registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
    private var enc: Encoding = registry.getEncoding(EncodingType.CL100K_BASE)

    private val language: LanguageService = LanguageService()

    private val supportedExtensions: Set<String> = SupportedLang.all().map {
        language.getExtension(it.name.lowercase())
    }.toSet()

    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(ThresholdChecker::class.java)

    /**
     * Checks if the given job meets the threshold criteria for processing.
     *
     * @param job The instruction file job to be checked.
     * @return Returns true if the job meets the threshold criteria, false otherwise.
     */
    fun isMetThreshold(job: InstructionFileJob): Boolean {
        val summary = job.fileSummary
        if (!supportedExtensions.contains(summary.extension)) {
            return false
        }

        if (summary.complexity > context.qualityThreshold.complexity) {
            logger.info("skip file ${summary.location} for complexity ${summary.complexity}")
            return false
        }

        // like js minified file
        if (summary.binary || summary.generated || summary.minified) {
            return false
        }

        // if the file size is too large, we just try 64k
        if (summary.bytes > context.qualityThreshold.fileSize) {
            logger.info("skip file ${summary.location} for size ${summary.bytes}")
            return false
        }

        // limit by token length
        val encoded = enc.encode(job.code)
        val length = encoded.size
        if (length > context.qualityThreshold.maxTokenLength) {
            logger.info("skip file ${summary.location} for over ${context.qualityThreshold.maxTokenLength} tokens")
            println("| filename: ${summary.filename} |  tokens: $length | complexity: ${summary.complexity} | code: ${summary.lines} | size: ${summary.bytes} | location: ${summary.location} |")
            return false
        }

        return true
    }

    /**
     * Determines whether the given instruction meets the threshold criteria.
     *
     * @param ins the instruction to be evaluated
     * @return true if the instruction meets the threshold criteria, false otherwise
     */
    fun isMetThreshold(ins: Instruction): Boolean {
        val totalToken = enc.encode(ins.instruction + ins.input + ins.output).size
        return totalToken <= context.qualityThreshold.maxTokenLength
    }
}