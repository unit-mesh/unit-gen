package cc.unitmesh.pick.worker

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.threshold.ThresholdChecker
import cc.unitmesh.pick.worker.base.LangWorker
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.lang.JavaWorker
import cc.unitmesh.pick.worker.lang.TypescriptWorker
import org.archguard.scanner.analyser.ScaAnalyser
import org.archguard.scanner.core.client.ArchGuardClient
import org.archguard.scanner.core.client.EmptyArchGuardClient
import org.archguard.scanner.core.sca.ScaContext
import org.slf4j.Logger
import java.io.File
import java.util.*


class WorkerManager(private val context: WorkerContext) {
    private val workers: Map<SupportedLang, LangWorker> = mapOf(
        SupportedLang.JAVA to JavaWorker(context),
        SupportedLang.TYPESCRIPT to TypescriptWorker(context),
    )

    private val thresholdChecker: ThresholdChecker = ThresholdChecker(context)

    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(WorkerManager::class.java)

    /**
     * Initializes the project by scanning the specified code directory to retrieve the dependencies.
     *
     * @param codeDir The directory where the project code is stored.
     * @param language The programming language used in the project.
     *
     */
    fun init(codeDir: File, language: String) {
        try {
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

            context.compositionDependency = dependencies
        } catch (e: Exception) {
            logger.error("failed to init dependencies", e)
        }
    }

    /**
     * Adds a job to the worker for processing based on the specified threshold.
     *
     * @param job The job to be added. It should be an instance of InstructionFileJob.
     *
     * @return true if the job is successfully added to the worker, false otherwise.
     */
    fun tryAddJobByThreshold(job: InstructionFileJob): Boolean {
        if (!thresholdChecker.isMetThreshold(job)) {
            return false
        }

        val language = SupportedLang.from(job.fileSummary.language)
        val worker = workers[language] ?: return false
        worker.prepareJob(job)

        return true
    }

    suspend fun runAll(): List<Instruction> {
        val results = workers.map { (_, worker) ->
            try {
                worker.start()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }.flatten()

        // take context.completionTypeSize for each type
        val finalList: EnumMap<CompletionBuilderType, List<Instruction>> =
            EnumMap(CompletionBuilderType::class.java)

        var skipCount = 0
        // filter output by threshold
        results.mapNotNull {
            val ins = it.unique()
            if (thresholdChecker.isMetThreshold(ins)) {
                finalList[it.type] = finalList[it.type]?.plus(ins) ?: listOf(ins)
            } else {
                skipCount++
                null
            }
        }

        if (skipCount > 0) {
            logger.info("skip $skipCount / ${results.size} instructions, ")
        }

        // take context.completionTypeSize for each type
        return finalList.keys.map {
            finalList[it]?.take(context.completionTypeSize) ?: emptyList()
        }.flatten()
    }
}
