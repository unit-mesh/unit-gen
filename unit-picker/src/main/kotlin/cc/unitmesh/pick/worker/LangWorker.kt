package cc.unitmesh.pick.worker

import cc.unitmesh.pick.config.SingleFileInstructionJob
import cc.unitmesh.pick.prompt.Instruction

abstract class LangWorker {
    abstract fun addJob(job: SingleFileInstructionJob)
    abstract suspend fun start() : Collection<Instruction>
}