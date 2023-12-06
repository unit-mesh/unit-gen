package cc.unitmesh.pick.worker

abstract class AbstractWorker {
    abstract suspend fun start()
}