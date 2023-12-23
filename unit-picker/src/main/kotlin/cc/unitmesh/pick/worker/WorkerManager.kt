package cc.unitmesh.pick.worker

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.worker.base.LangWorker
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.lang.JavaWorker
import org.archguard.scanner.analyser.ScaAnalyser
import org.archguard.scanner.analyser.count.LanguageService
import org.archguard.scanner.core.client.ArchGuardClient
import org.archguard.scanner.core.client.EmptyArchGuardClient
import org.archguard.scanner.core.sca.ScaContext
import org.slf4j.Logger
import java.io.File


class WorkerManager(private val workerContext: WorkerContext) {
    private val workers: Map<SupportedLang, LangWorker> = mapOf(
        SupportedLang.JAVA to JavaWorker(workerContext),
//        Language.TYPESCRIPT to TypescriptWorker(workerContext),
//        Language.JAVASCRIPT to TypescriptWorker(workerContext),
    )

    private val language: LanguageService = LanguageService()

    private val supportedExtensions: Set<String> = setOf(
        language.getExtension(SupportedLang.JAVA.name.lowercase()),
    )

    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(WorkerManager::class.java)

    /**
     * Initializes the project by scanning the specified code directory to retrieve the dependencies.
     *
     * @param codeDir The directory where the project code is stored.
     * @param language The programming language used in the project.
     *
     */
    fun init(codeDir: File, language: String) {
        val dependencies = ScaAnalyser(object : ScaContext {
            override val client: ArchGuardClient = EmptyArchGuardClient()
            override val language: String = language
            override val path: String = codeDir.absolutePath
        }).analyse()

        if (dependencies.isEmpty()) {
            logger.warn("no dependencies found in $codeDir")
        } else {
            logger.info("found ${dependencies.size} dependencies in $codeDir")
        }

        workerContext.compositionDependency = dependencies
        workerContext.testFramework = TestFrameworkIdentifier(language, dependencies).identify()
    }

    /**
     * Adds a job to the worker for processing based on the specified threshold.
     *
     * @param job The job to be added.
     *
     * @return None.
     */
    fun addJobByThreshold(job: InstructionFileJob) {
        val summary = job.fileSummary
        if (!supportedExtensions.contains(summary.extension)) {
            return
        }

        if (summary.complexity > workerContext.insQualityThreshold.complexity) {
            logger.info("skip file ${summary.location} for complexity ${summary.complexity}")
            return;
        }

        if (summary.binary || summary.generated || summary.minified) {
            return
        }

        // if the file size is too large, we just try 64k
        if (summary.bytes > workerContext.insQualityThreshold.fileSize) {
            logger.info("skip file ${summary.location} for size ${summary.bytes}")
            return
        }

//        val encoded = enc.encode(job.code)
//        val length = encoded.size
//        if (length > 4000) {
////            logger.info("skip file ${summary.location} for over 4000 tokens")
//            println("| filename: ${summary.filename} |  tokens: $length | complexity: ${summary.complexity} | code: ${summary.lines} | size: ${summary.bytes} | location: ${summary.location} |")
//            return
//        }

        val language = SupportedLang.from(summary.language)
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
