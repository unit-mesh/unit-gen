package cc.unitmesh.pick.worker

import cc.unitmesh.pick.picker.PickJob
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.archguard.rule.common.Language

class WorkerManager {
    private val workers: Map<Language, LangWorker> = mapOf(
        Language.JAVA to JavaLangWorker(),
    )

    fun addJob(job: PickJob) {
        val language = job.language.toSupportLanguage()
//     print job serial
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