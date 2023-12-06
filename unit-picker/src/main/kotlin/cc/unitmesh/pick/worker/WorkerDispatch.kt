package cc.unitmesh.pick.worker

import org.archguard.rule.common.Language

object WorkerDispatch {
    /**
     * Dispatch language worker by language
     */
    fun worker(language: Language): AbstractWorker? {
        return when (language) {
            Language.JAVA -> JavaFileWorker()
            else -> null
        }
    }
}