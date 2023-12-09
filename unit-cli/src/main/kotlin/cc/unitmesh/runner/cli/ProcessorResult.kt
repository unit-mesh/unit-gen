package cc.unitmesh.runner.cli

import cc.unitmesh.pick.prompt.Instruction

data class ProcessorResult(
    val repository: String,
    val content: MutableList<Instruction>,
    val outputName: String,
)