package cc.unitmesh.pick.threshold

import cc.unitmesh.core.Instruction
import cc.unitmesh.pick.threshold.filter.*
import cc.unitmesh.pick.threshold.pipeline.Pipeline
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.job.FileSummary
import cc.unitmesh.pick.worker.job.InstructionFileJob
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.EncodingRegistry
import com.knuddels.jtokkit.api.EncodingType

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

    private val pipeline: Pipeline<FileSummary>
        get() {
            return Pipeline<FileSummary>()
                .addFilter(ExtensionFilter(context.qualityThreshold))
                .addFilter(ComplexityFilter(context.qualityThreshold))
                .addFilter(BinaryGeneratedMinifiedFilter(context.qualityThreshold))
                .addFilter(SizeFilter(context.qualityThreshold))
                .addFilter(TokenLengthFilter(context.qualityThreshold))
        }

    /**
     * Checks if the given job meets the threshold criteria for processing.
     *
     * @param job The instruction file job to be checked.
     * @return Returns true if the job meets the threshold criteria, false otherwise.
     */
    fun isMetThreshold(job: InstructionFileJob): Boolean {
        return pipeline.process(job.fileSummary)
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
