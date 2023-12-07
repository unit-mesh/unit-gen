package cc.unitmesh.pick.worker

import cc.unitmesh.pick.picker.PickJob
import org.archguard.rule.common.Language

class WorkerManager {
    private val workers: Map<Language, LangWorker> = mapOf(
        // todo: add more language support
        Language.JAVA to JavaLangWorker(),
    )

    fun addJob(job: PickJob) {
        val language = job.fileSummary.language.toSupportLanguage()
        val worker = workers[language]
        worker?.addJob(job)
    }

    suspend fun runAll() {
        workers.forEach { (_, worker) ->
            worker.start()
        }
    }
}

private fun String.toSupportLanguage(): Language? {
    return when (this.lowercase()) {
        "java" -> Language.JAVA
        "kotlin" -> Language.KOTLIN
        "csharp", "c#" -> Language.CSHARP
        "python" -> Language.PYTHON
        "go" -> Language.GO
        "typescript" -> Language.TYPESCRIPT
        "javascript" -> Language.JAVASCRIPT
        else -> null
    }
}