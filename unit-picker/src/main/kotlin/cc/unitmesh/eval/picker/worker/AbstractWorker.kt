package cc.unitmesh.eval.picker.worker

abstract class AbstractWorker {
    abstract suspend fun start(filePath: String)
}