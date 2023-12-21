package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.builder.BuilderConfig
import cc.unitmesh.pick.builder.InstructionFileJob
import cc.unitmesh.quality.CodeQualityType

data class JobContext(
    val job: InstructionFileJob,
    val qualityTypes: List<CodeQualityType>,
    val fileTree: HashMap<String, InstructionFileJob>,
    val builderConfig: BuilderConfig,
    val completionBuilderTypes: List<CompletionBuilderType>,
    val maxCompletionInOneFile: Int
)