package cc.unitmesh.pick.worker.job

import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.quality.CodeQualityType
import kotlinx.serialization.Serializable
import org.archguard.scanner.analyser.count.FileJob


@Serializable
data class JobContext(
    val job: InstructionFileJob,
    val qualityTypes: List<CodeQualityType>,
    val fileTree: HashMap<String, InstructionFileJob>,
    val insOutputConfig: InsOutputConfig = InsOutputConfig(),
    val completionBuilderTypes: List<CompletionBuilderType>,
    val maxCompletionInOneFile: Int,
    val project: ProjectContext = ProjectContext(),
) {
    companion object {
        fun default(fileTree: HashMap<String, InstructionFileJob> = hashMapOf()): JobContext {
            return JobContext(
                job = InstructionFileJob(
                    fileSummary = FileJob()
                ),
                qualityTypes = listOf(),
                fileTree = fileTree,
                completionBuilderTypes = listOf(),
                maxCompletionInOneFile = 3
            )
        }
    }
}