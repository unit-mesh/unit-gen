package cc.unitmesh.pick.worker

import cc.unitmesh.core.Instruction
import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.threshold.ThresholdChecker
import cc.unitmesh.pick.worker.base.LangWorker
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.lang.JavaWorker
import cc.unitmesh.pick.worker.lang.KotlinWorker
import cc.unitmesh.pick.worker.lang.RustWorker
import cc.unitmesh.pick.worker.lang.TypescriptWorker
import org.archguard.scanner.analyser.ScaAnalyser
import org.archguard.scanner.core.client.ArchGuardClient
import org.archguard.scanner.core.client.EmptyArchGuardClient
import org.archguard.scanner.core.sca.ScaContext
import org.slf4j.Logger
import java.io.File
import java.util.*


class WorkerManager(private val context: WorkerContext) {
    private val workers: Map<SupportedLang, LangWorker> = SupportedLang.all().map {
        it to when (it) {
            SupportedLang.JAVA -> JavaWorker(context)
            SupportedLang.KOTLIN -> KotlinWorker(context)
            SupportedLang.TYPESCRIPT -> TypescriptWorker(context)
            SupportedLang.RUST -> RustWorker(context)
        }
    }.toMap()

    private val thresholdChecker: ThresholdChecker = ThresholdChecker(context)

    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(WorkerManager::class.java)

    /**
     * Initializes the project by scanning the specified code directory to retrieve the dependencies.
     *
     * @param codeDir The directory where the project code is stored.
     * @param language The programming language used in the project.
     *
     */
    fun init(codeDir: File, language: SupportedLang) {
        try {
            val dependencies = ScaAnalyser(object : ScaContext {
                override val client: ArchGuardClient = EmptyArchGuardClient()
                override val language: String = language.name.lowercase()
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
    fun tryAddJobByThreshold(job: InstructionFileJob, supportedLangs: SupportedLang): Boolean {
        if (!thresholdChecker.isMetThreshold(job)) {
            return false
        }

        val parsedLanguage = SupportedLang.from(job.fileSummary.language) ?: return false
        if (parsedLanguage != supportedLangs) {
            return false
        }

        val worker = workers[parsedLanguage] ?: return false
        worker.prepareJob(job)

        return true
    }

    /**
     * Executes all workers and returns a list of instructions.
     *
     * This method starts all the workers asynchronously and collects the results. If any worker encounters an exception
     * during execution, it will be caught and an empty list will be returned for that worker. The results from all workers
     * are then flattened into a single list.
     *
     * The method then filters the output based on a threshold. Each instruction is checked against the threshold checker,
     * and if it meets the threshold, it is added to the final list. Instructions that do not meet the threshold are skipped.
     *
     * The final list is a map that groups instructions by their completion builder type. Each type has a list of instructions
     * associated with it. The final list is created using an EnumMap, where the keys are the completion builder types.
     *
     * If any instructions were skipped due to not meeting the threshold, a log message is printed indicating the number of
     * skipped instructions and the total number of instructions.
     *
     * Finally, the final list is shuffled and truncated to the desired completion type size for each type. The shuffled and
     * truncated lists are then flattened into a single list and returned.
     *
     * @return a list of instructions after executing all workers and applying the threshold filter
     */
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
            logger.info("skip $skipCount / ${results.size} instructions of ${context.pureDataFileName} ")
        }

        // take context.completionTypeSize for each type
        return finalList.keys.map {
            finalList[it]?.shuffled()?.take(context.completionTypeSize) ?: emptyList()
        }.flatten()
    }
}
