package cc.unitmesh.pick.worker

import cc.unitmesh.pick.builder.InstructionFileJob
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.worker.worker.JavaWorker
import org.archguard.rule.common.Language
import org.archguard.scanner.analyser.count.LanguageService
import org.slf4j.Logger


class WorkerManager(workerContext: WorkerContext) {
    private val workers: Map<Language, LangWorker> = mapOf(
        Language.JAVA to JavaWorker(workerContext),
//        Language.TYPESCRIPT to TypescriptWorker(workerContext),
//        Language.JAVASCRIPT to TypescriptWorker(workerContext),
    )

    private val language: LanguageService = LanguageService()

    private val supportedExtensions: Set<String> = setOf(
        language.getExtension(Language.JAVA.name.lowercase()),
    )

//    var registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
//    var enc: Encoding = registry.getEncoding(EncodingType.CL100K_BASE)

    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(WorkerManager::class.java)

    fun addJob(job: InstructionFileJob) {
        val summary = job.fileSummary
        if (!supportedExtensions.contains(summary.extension)) {
            return
        }

        if (summary.complexity > 100) {
            logger.info("skip file ${summary.location} for complexity ${summary.complexity}")
// TODO: add debugging option
//            if (summary.filename.endsWith(".java")) {
//                println("| filename: ${summary.filename} | complexity: ${summary.complexity} | code: ${summary.lines} | size: ${summary.bytes} | location: ${summary.location} |")
//            }
            return;
        }
        if (summary.binary || summary.generated || summary.minified) {
            return
        }

        // if the file size is too large, we just try 64k
        if (summary.bytes > 1024 * 64) {
            logger.info("skip file ${summary.location} for size ${summary.bytes}")
// TODO: add debugging option
//            if (summary.filename.endsWith(".java")) {
//                println("| filename: ${summary.filename} | complexity: ${summary.complexity} | code: ${summary.lines} | size: ${summary.bytes} | location: ${summary.location} |")
//            }
            return
        }

//        val encoded = enc.encode(job.code)
//        val length = encoded.size
//        if (length > 4000) {
////            logger.info("skip file ${summary.location} for over 4000 tokens")
//            println("| filename: ${summary.filename} |  tokens: $length | complexity: ${summary.complexity} | code: ${summary.lines} | size: ${summary.bytes} | location: ${summary.location} |")
//            return
//        }

        val language = summary.language.toSupportLanguage()
        val worker = workers[language] ?: return
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