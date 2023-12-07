package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.picker.InstructionJob

data class InstructionContext(
    val job: InstructionJob
)

interface InstructionBuilder {
    fun build(context: InstructionContext): Instruction
}


