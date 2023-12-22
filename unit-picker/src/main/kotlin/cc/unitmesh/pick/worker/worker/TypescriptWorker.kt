package cc.unitmesh.pick.worker.worker

import cc.unitmesh.pick.builder.InstructionFileJob
import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.pick.prompt.completion.CompletionBuilderType
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.worker.JobContext
import cc.unitmesh.pick.prompt.ins.base.TypedCompletionIns
import cc.unitmesh.pick.worker.LangWorker
import cc.unitmesh.pick.worker.WorkerContext
import chapi.ast.typescriptast.TypeScriptAnalyser
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class TypescriptWorker(private val context: WorkerContext) : LangWorker() {
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
                JobContext(job, context.qualityTypes, fileTree, context.builderConfig, context.completionTypes, 3)

            context.codeContextStrategies.map { type ->
                type.builder(jobContext).build()
            }.flatten()
        }.flatten()

        // take context.completionTypeSize for each type
        val finalList: EnumMap<CompletionBuilderType, TypedCompletionIns> =
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