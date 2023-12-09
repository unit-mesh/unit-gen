package cc.unitmesh.pick.worker

import cc.unitmesh.pick.config.BuilderConfig
import cc.unitmesh.pick.prompt.InstructionType
import cc.unitmesh.quality.CodeQualityType

data class WorkerContext(
    val instructionTypes: List<InstructionType>,
    val qualityTypes: List<CodeQualityType>,
    val builderConfig: BuilderConfig,
)
