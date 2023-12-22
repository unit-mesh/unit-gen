package cc.unitmesh.pick.worker.base

import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.core.Instruction

interface LangWorker {
    fun addJob(job: InstructionFileJob)
    suspend fun start() : Collection<Instruction>
}