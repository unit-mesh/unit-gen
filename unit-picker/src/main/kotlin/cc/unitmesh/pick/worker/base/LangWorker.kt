package cc.unitmesh.pick.worker.base

import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.project.ProjectContext
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.job.JobContext
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import java.io.File

interface LangWorker {
    val context: WorkerContext
    val fileTree: HashMap<String, InstructionFileJob>
    val logger: Logger
    val jobs: MutableList<InstructionFileJob>

    /**
     * Prepares the given instruction file job for execution.
     *
     * @param job The instruction file job to be prepared.
     */
    fun prepareJob(job: InstructionFileJob)

    /**
     * Starts the execution of the job processing.
     *
     * This method processes each job in the `jobs` list and writes the output to a file specified in the
     * `pureDataFileName` property of the `context`.
     *
     * If the file does not exist, it will be created. If there are any errors while creating the file,
     * an error message will be logged and an empty list will be returned.
     *
     * The method uses a coroutine scope for suspending the execution and allows concurrent processing of jobs.
     *
     * @return a list of `TypedIns` objects representing the output of the job processing. An empty list
     * will be returned if there are any errors.
     */
    suspend fun start(): List<TypedIns> = coroutineScope {
        val outputFile = File(context.pureDataFileName)
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile()
            } catch (e: Exception) {
                logger.error("create file error: $outputFile")
                e.printStackTrace()
                return@coroutineScope emptyList()
            }
        }

        val result = jobs.map { job ->
            val jobContext = JobContext(
                job,
                context.qualityTypes,
                fileTree,
                context.insOutputConfig,
                context.completionTypes,
                context.maxCompletionInOneFile,
                project = ProjectContext(
                    compositionDependency = context.compositionDependency,
                ),
                context.qualityThreshold
            )

            val flatten = context.codeContextStrategies.map { type ->
                type.builder(jobContext).build()
            }.flatten()

            flatten.map {
                outputFile.appendText(it.toString() + "\n")
            }

            flatten
        }.flatten()

        return@coroutineScope result
    }

}