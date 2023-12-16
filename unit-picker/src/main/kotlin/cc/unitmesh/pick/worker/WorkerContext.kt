package cc.unitmesh.pick.worker

import cc.unitmesh.pick.config.BuilderConfig
import cc.unitmesh.pick.prompt.CodeContextStrategy
import cc.unitmesh.quality.CodeQualityType

data class WorkerContext(
    val codeContextStrategies: List<CodeContextStrategy>,
    val qualityTypes: List<CodeQualityType>,
    val builderConfig: BuilderConfig,
    val pureDataFileName: String,
)
