package cc.unitmesh.pick.worker

import cc.unitmesh.pick.picker.PickJob

abstract class AbstractWorker {
    /**
     * Start worker
     */
    abstract suspend fun start(job: PickJob)
}