package cc.unitmesh.pick.worker

import cc.unitmesh.pick.config.BuilderConfig
import cc.unitmesh.pick.prompt.RelatedType
import cc.unitmesh.quality.CodeQualityType

data class WorkerContext(
    val relatedTypes: List<RelatedType>,
    val qualityTypes: List<CodeQualityType>,
    val builderConfig: BuilderConfig,
    val pureDataFileName: String,
)
