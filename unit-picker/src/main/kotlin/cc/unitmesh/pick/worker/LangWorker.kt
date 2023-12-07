package cc.unitmesh.pick.worker

import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.picker.InstructionJob

abstract class LangWorker {
    abstract fun addJob(job: InstructionJob)
    abstract suspend fun start() : List<InstructionBuilder>
}