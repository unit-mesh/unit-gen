package cc.unitmesh.pick.worker

import cc.unitmesh.pick.builder.BuilderConfig
import cc.unitmesh.pick.prompt.CodeContextStrategy
import cc.unitmesh.pick.prompt.base.CompletionBuilderType
import cc.unitmesh.pick.threshold.QualityThreshold
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
    val qualityThreshold: QualityThreshold = QualityThreshold(),
)

