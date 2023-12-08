package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.picker.InstructionJob

data class InstructionContext(
    val job: InstructionJob,
    val fileTree: HashMap<String, InstructionJob>,
)

interface InstructionBuilder<T> {
    fun convert(): T
    fun build(): List<Instruction>

    companion object {
        fun build(
            types: List<InstructionType>,
            fileTree: HashMap<String, InstructionJob>,
            job: InstructionJob,
        ): Collection<Instruction> {
            val instructionContext = InstructionContext(job, fileTree)

            return types.map { type ->
                type.builder(instructionContext).build()
            }.flatten()
        }
    }
}


