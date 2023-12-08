package cc.unitmesh.pick.worker

import cc.unitmesh.pick.prompt.InstructionType
import cc.unitmesh.quality.CodeQualityType

data class WorkerContext(
    val builderTypes: List<InstructionType>,
    val qualityTypes: List<CodeQualityType>
)
