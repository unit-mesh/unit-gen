package cc.unitmesh.runner.cli

import cc.unitmesh.pick.prompt.Instruction
import kotlinx.serialization.Serializable

@Serializable
data class ProcessorResult(
    val repository: String,
    val content: MutableList<Instruction>,
    val outputName: String,
)