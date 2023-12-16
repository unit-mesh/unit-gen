package cc.unitmesh.pick.worker

import cc.unitmesh.pick.builder.BuilderConfig
import cc.unitmesh.pick.prompt.CodeContextStrategy
import cc.unitmesh.quality.CodeQualityType

data class WorkerContext(
    val codeContextStrategies: List<CodeContextStrategy>,
    val qualityTypes: List<CodeQualityType>,
    val builderConfig: BuilderConfig,
    val pureDataFileName: String,
)
