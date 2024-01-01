package cc.unitmesh.runner.cli

import cc.unitmesh.core.Instruction
import kotlinx.serialization.Serializable

@Serializable
data class ProcessorResult(
    val repository: String,
    val content: List<Instruction>,
    val outputName: String,
)
