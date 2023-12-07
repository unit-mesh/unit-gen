package cc.unitmesh.pick.worker

import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.picker.PickJob
import org.archguard.rule.common.Language

class WorkerManager {
    private val workers: Map<Language, LangWorker> = mapOf(
        Language.JAVA to JavaLangWorker(),
    )

    fun addJob(job: PickJob) {
        val language = job.fileSummary.language.toSupportLanguage()
        val worker = workers[language]
        worker?.addJob(job)
    }

    suspend fun runAll() : List<InstructionBuilder> {
        return workers.map { (_, worker) ->
            worker.start()
        }.flatten()
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