package cc.unitmesh.pick.threshold

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.job.FileSummary
import cc.unitmesh.pick.worker.job.InstructionFileJob
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.EncodingRegistry
import com.knuddels.jtokkit.api.EncodingType
import org.archguard.scanner.analyser.count.LanguageService

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

    /**
     * Checks if the given job meets the threshold criteria for processing.
     *
     * @param job The instruction file job to be checked.
     * @return Returns true if the job meets the threshold criteria, false otherwise.
     */
    fun isMetThreshold(job: InstructionFileJob): Boolean {
        val summary = job.fileSummary
        val qualityThreshold = context.qualityThreshold

        val pipeline = Pipeline<FileSummary>()
        pipeline.addFilter(ExtensionFilter(qualityThreshold))
        pipeline.addFilter(ComplexityFilter(qualityThreshold))
        pipeline.addFilter(BinaryGeneratedMinifiedFilter(qualityThreshold))
        pipeline.addFilter(SizeFilter(qualityThreshold))
        pipeline.addFilter(TokenLengthFilter(qualityThreshold))

        return pipeline.process(summary)
    }

    /**
     * Determines whether the given instruction meets the threshold criteria.
     *
     * @param ins the instruction to be evaluated
     * @return true if the instruction meets the threshold criteria, false otherwise
     */
    fun isMetThreshold(ins: Instruction): Boolean {
        // skip empty instruction
        if (ins.input.isEmpty() || ins.output.isEmpty()) {
            return false
        }

        // limit by token length
        val totalToken = enc.encode(ins.instruction + ins.input + ins.output).size
        return totalToken <= context.qualityThreshold.maxTokenLength
    }
}

class ExtensionFilter(private val qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    private val language: LanguageService = LanguageService()

    private val supportedExtensions: Set<String> = SupportedLang.all().map {
        language.getExtension(it.extension)
    }.toSet()

    override fun filter(data: FileSummary): Boolean {
        return supportedExtensions.contains(data.extension)
    }
}

class ComplexityFilter(private val qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    override fun filter(data: FileSummary): Boolean {
        return data.complexity <= qualityThreshold.complexity
    }
}

class BinaryGeneratedMinifiedFilter(qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    override fun filter(data: FileSummary): Boolean {
        return !(data.binary || data.generated || data.minified)
    }
}

class SizeFilter(private val qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    override fun filter(data: FileSummary): Boolean {
        return data.bytes <= qualityThreshold.fileSize
    }
}

class TokenLengthFilter(private val qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    private var registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
    private var enc: Encoding = registry.getEncoding(EncodingType.CL100K_BASE)

    override fun filter(data: FileSummary): Boolean {
        val encoded = enc.encode(data.content.toString())
        val length = encoded.size
        val codeWithBuffer = 1.25
        return length <= qualityThreshold.maxTokenLength * codeWithBuffer
    }
}
