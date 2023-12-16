package cc.unitmesh.pick.worker.worker

import cc.unitmesh.pick.config.InstructionFileJob
import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionContext
import cc.unitmesh.pick.worker.LangWorker
import cc.unitmesh.pick.worker.WorkerContext
import chapi.ast.typescriptast.TypeScriptAnalyser
import kotlinx.coroutines.coroutineScope
import java.io.File

class TypescriptWorker(private val context: WorkerContext) : LangWorker() {
    private val jobs: MutableList<InstructionFileJob> = mutableListOf()
    private val fileTree: HashMap<String, InstructionFileJob> = hashMapOf()

    override fun addJob(job: InstructionFileJob) {
        this.jobs.add(job)

        // since the Java Analyser imports will be in data structures
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
    }

    override suspend fun start(): Collection<Instruction> = coroutineScope {
        val file = File(context.pureDataFileName)
        if (!file.exists()) {
            file.createNewFile()
        }

        val lists = jobs.map { job ->
            val instructionContext = InstructionContext(job, context.qualityTypes, fileTree, context.builderConfig)

            context.codeContextStrategies.map { type ->
                val instructionBuilder = type.builder(instructionContext)
                val list = instructionBuilder.build()
                list.map {
                    file.appendText(it.toString() + "\n")
                }
                instructionBuilder.unique(list as List<Nothing>)
            }.flatten()
        }.flatten()

        return@coroutineScope lists
    }
}