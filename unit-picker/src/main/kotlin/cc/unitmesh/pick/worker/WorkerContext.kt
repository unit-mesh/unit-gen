package cc.unitmesh.pick.worker

import cc.unitmesh.pick.prompt.InstructionType

data class WorkerContext(
    val builderType: List<InstructionType>,
)
