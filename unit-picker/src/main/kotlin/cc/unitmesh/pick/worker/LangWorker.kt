package cc.unitmesh.pick.worker

import cc.unitmesh.pick.output.Instruction
import cc.unitmesh.pick.picker.PickJob

abstract class LangWorker {
    abstract fun addJob(job: PickJob)
    abstract suspend fun start() : List<Instruction>
}