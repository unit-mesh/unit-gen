package cc.unitmesh.pick.worker.job

import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.project.ProjectContext
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.quality.CodeQualityType
import kotlinx.serialization.Serializable
import org.archguard.scanner.analyser.count.FileJob
import org.jetbrains.annotations.TestOnly

@Serializable
data class JobContext(
    val job: InstructionFileJob,
    val qualityTypes: List<CodeQualityType>,
    val fileTree: HashMap<String, InstructionFileJob>,
    val insOutputConfig: InsOutputConfig = InsOutputConfig(),
    val instructionBuilderTypes: List<InstructionBuilderType>,
    val maxTypedCompletionSize: Int,
    val project: ProjectContext = ProjectContext(),
    val insQualityThreshold: InsQualityThreshold,
) {
    companion object {
        @TestOnly
        fun default(fileTree: HashMap<String, InstructionFileJob> = hashMapOf()): JobContext {
            return JobContext(
                job = InstructionFileJob(
                    fileSummary = FileJob()
                ),
                qualityTypes = listOf(),
                fileTree = fileTree,
                instructionBuilderTypes = listOf(),
                maxTypedCompletionSize = InsQualityThreshold.MAX_PROJECT_TYPED_COMPLETION_SIZE,
                insQualityThreshold = InsQualityThreshold()
            )
        }
    }
}