package cc.unitmesh.pick.worker

import cc.unitmesh.pick.builder.InstructionFileJob
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.worker.worker.JavaWorker
import cc.unitmesh.pick.worker.worker.TypescriptWorker
import org.archguard.rule.common.Language
import org.slf4j.Logger

class WorkerManager(workerContext: WorkerContext) {
    private val workers: Map<Language, LangWorker> = mapOf(
        Language.JAVA to JavaWorker(workerContext),
//        Language.TYPESCRIPT to TypescriptWorker(workerContext),
//        Language.JAVASCRIPT to TypescriptWorker(workerContext),
    )

    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(WorkerManager::class.java)

    fun addJob(job: InstructionFileJob) {
        if (job.fileSummary.complexity > 100) {
            logger.info("skip file ${job.fileSummary.location} for complexity ${job.fileSummary.complexity}")
            return;
        }
        if (job.fileSummary.binary || job.fileSummary.generated || job.fileSummary.minified) {
            return
        }

        // if the file size is too large, we just try 64k
        if (job.fileSummary.bytes > 1024 * 64) {
            logger.info("skip file ${job.fileSummary.location} for size ${job.fileSummary.bytes}")
            return;
        }

        val language = job.fileSummary.language.toSupportLanguage()
        val worker = workers[language] ?: return
        logger.info("add file:" + job.fileSummary.location)
        worker.addJob(job)
    }

    suspend fun runAll(): List<Instruction> {
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