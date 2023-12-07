package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.picker.InstructionJob

data class InstructionContext(
    val job: InstructionJob,
)

interface InstructionBuilder {
    fun build(): List<Instruction>

    companion object {
        fun build(types: List<InstructionType>, job: InstructionJob): Collection<Instruction> {
            return types.map { type ->
                type.builder(InstructionContext(job)).build()
            }.flatten()
        }
    }
}


