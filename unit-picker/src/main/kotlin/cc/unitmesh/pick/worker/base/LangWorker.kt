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

    fun addJob(job: InstructionFileJob)
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