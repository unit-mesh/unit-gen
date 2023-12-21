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
    val qualityThreshold: QualityThreshold = QualityThreshold(),
)

@Serializable
data class QualityThreshold(
    val complexity: Long = MAX_COMPLEXITY,
    val fileSize: Long = MAX_FILE_SIZE,
    val maxLineInCode: Int = 160,
    val maxCharInCode: Int = 3000,
) {
    companion object {
        const val MAX_COMPLEXITY: Long = 100
        const val MAX_FILE_SIZE: Long = 1024 * 64
    }
}
