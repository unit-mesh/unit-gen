package cc.unitmesh.pick.worker

import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.strategy.BizCodeContextStrategy
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.option.InsQualityThreshold
import cc.unitmesh.quality.CodeQualityType
import kotlinx.serialization.Serializable
import org.archguard.scanner.core.sca.CompositionDependency

@Serializable
data class WorkerContext(
    val codeContextStrategies: List<BizCodeContextStrategy>,
    val qualityTypes: List<CodeQualityType>,
    val insOutputConfig: InsOutputConfig,
    val pureDataFileName: String,
    val completionTypes: List<CompletionBuilderType>,
    val maxCompletionInOneFile: Int,
    val completionTypeSize: Int,
    val insQualityThreshold: InsQualityThreshold = InsQualityThreshold(),
    var compositionDependency: List<CompositionDependency> = listOf()
) {
    var testFramework: List<String> = listOf()
}

