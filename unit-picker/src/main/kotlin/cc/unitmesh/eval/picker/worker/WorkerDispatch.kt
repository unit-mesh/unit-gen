package cc.unitmesh.eval.picker.worker

object WorkerDispatch {
    /**
     * Dispatch language worker by language
     */
    fun worker(language: String): AbstractWorker? {
        return when (language) {
            "Java" -> JavaFileWorker()
            else -> null
        }
    }
}