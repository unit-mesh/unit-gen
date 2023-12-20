package cc.unitmesh.pick.worker

import cc.unitmesh.pick.builder.BuilderConfig
import cc.unitmesh.pick.prompt.CodeContextStrategy
import cc.unitmesh.pick.prompt.CompletionBuilderType
import cc.unitmesh.quality.CodeQualityType
import kotlinx.serialization.Serializable

@Serializable
data class WorkerContext(
    val codeContextStrategies: List<CodeContextStrategy>,
    val qualityTypes: List<CodeQualityType>,
    val builderConfig: BuilderConfig,
    val pureDataFileName: String,
    val completionTypes: List<CompletionBuilderType>,
    val maxCompletionInOneFile: Int,
    val completionTypeSize: Int,
)
@Serializable
data class QualityThreshold(
    val complexity: Long = 100,
    val fileSize: Long = 1024 * 64,
)
