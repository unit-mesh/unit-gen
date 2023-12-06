package cc.unitmesh.pick.worker

import cc.unitmesh.pick.picker.PickJob

object WorkerDispatch {
    /**
     * Dispatch language worker by language
     */
    fun worker(job: PickJob): AbstractWorker? {
        return when (job.language.lowercase()) {
            "java" -> JavaFileWorker(job)
            else -> null
        }
    }
}