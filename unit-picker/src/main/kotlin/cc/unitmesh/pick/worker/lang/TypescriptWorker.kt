package cc.unitmesh.pick.worker.lang

import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.Instruction
import cc.unitmesh.pick.worker.job.JobContext
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.base.LangWorker
import chapi.ast.typescriptast.TypeScriptAnalyser
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class TypescriptWorker(private val context: WorkerContext) : LangWorker {
    private val jobs: MutableList<InstructionFileJob> = mutableListOf()
    private val fileTree: HashMap<String, InstructionFileJob> = hashMapOf()

    override fun addJob(job: InstructionFileJob) {
        this.jobs.add(job)

        // since the Java Analyser imports will be in data structures
        try {

            val container = TypeScriptAnalyser().analysis(job.code, job.fileSummary.location)
            job.codeLines = job.code.lines()
            container.DataStructures.map { ds ->
                ds.Imports = container.Imports

                ds.Content = CodeDataStructUtil.contentByPosition(job.codeLines, ds.Position)
                ds.Functions.map {
                    it.apply {
                        it.Content = CodeDataStructUtil.contentByPosition(job.codeLines, it.Position)
                    }
                }
            }

            job.container = container
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun start(): Collection<Instruction> = coroutineScope {
        val outputFile = File(context.pureDataFileName)
        if (!outputFile.exists()) {
            outputFile.createNewFile()
        }

        val lists = jobs.map { job ->
            val jobContext =
                JobContext(
                    job,
                    context.qualityTypes,
                    fileTree,
                    context.insOutputConfig,
                    context.completionTypes,
                    3,
                    insQualityThreshold = context.insQualityThreshold
                )

            context.codeContextStrategies.map { type ->
                type.builder(jobContext).build()
            }.flatten()
        }.flatten()

        // take context.completionTypeSize for each type
        val finalList: EnumMap<CompletionBuilderType, TypedIns> =
            EnumMap(CompletionBuilderType::class.java)

        val instructions: MutableList<Instruction> = mutableListOf()

        lists.map {
            finalList[it.type] = it
        }

        val result = finalList.values.toMutableList().take(context.completionTypeSize)

        result.map {
            instructions.add(it.unique())
            outputFile.appendText(it.toString() + "\n")
        }

        return@coroutineScope instructions
    }
}