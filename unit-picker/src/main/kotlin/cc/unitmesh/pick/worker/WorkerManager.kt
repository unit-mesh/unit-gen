package cc.unitmesh.pick.worker

import cc.unitmesh.pick.builder.InstructionFileJob
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.worker.worker.JavaWorker
import cc.unitmesh.pick.worker.worker.TypescriptWorker
import org.archguard.rule.common.Language

class WorkerManager(workerContext: WorkerContext) {
    private val workers: Map<Language, LangWorker> = mapOf(
        Language.JAVA to JavaWorker(workerContext),
        Language.TYPESCRIPT to TypescriptWorker(workerContext),
        Language.JAVASCRIPT to TypescriptWorker(workerContext),
    )

    fun addJob(job: InstructionFileJob) {
        val language = job.fileSummary.language.toSupportLanguage()
        val worker = workers[language]
        worker?.addJob(job)
    }

    suspend fun runAll() : List<Instruction> {
        return workers.map { (_, worker) ->
            try {
                worker.start()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
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