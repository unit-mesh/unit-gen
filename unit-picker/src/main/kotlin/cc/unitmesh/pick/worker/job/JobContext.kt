package cc.unitmesh.pick.worker.job

import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.quality.CodeQualityType

data class JobContext(
    val job: InstructionFileJob,
    val qualityTypes: List<CodeQualityType>,
    val fileTree: HashMap<String, InstructionFileJob>,
    val insOutputConfig: InsOutputConfig,
    val completionBuilderTypes: List<CompletionBuilderType>,
    val maxCompletionInOneFile: Int
)