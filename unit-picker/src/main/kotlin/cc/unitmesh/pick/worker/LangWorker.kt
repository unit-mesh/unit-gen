package cc.unitmesh.pick.worker

import cc.unitmesh.pick.picker.InstructionJob
import cc.unitmesh.pick.prompt.Instruction

abstract class LangWorker {
    abstract fun addJob(job: InstructionJob)
    abstract suspend fun start() : Collection<Instruction>
}