package cc.unitmesh.pick.worker.worker

import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.pick.builder.InstructionFileJob
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.Instruction
import cc.unitmesh.pick.worker.JobContext
import cc.unitmesh.core.completion.TypedCompletionIns
import cc.unitmesh.pick.worker.LangWorker
import cc.unitmesh.pick.worker.WorkerContext
import chapi.ast.javaast.JavaAnalyser
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.EnumMap

/**
 * A repository will be like this:
 *
 * | CommitID    | AController | AService | BRepository| ARepository|
 * |------------|-------------|----------|------------|------------|
 * | a67c24fd   | 1           | 1        | 1          | 1           |
 * | b05d38f6   | 1           | 1        | 1          | 1           |
 * | 99ac469e   | 1           | 1        | 1          | 1           |
 *
 * We have different strategies to build the pick datasets.
 *
 * - by Horizontal (with Import File):
 * - by Vertical (with History Change):
 */
class JavaWorker(private val context: WorkerContext) : LangWorker() {
    private val jobs: MutableList<InstructionFileJob> = mutableListOf()
    private val fileTree: HashMap<String, InstructionFileJob> = hashMapOf()

    private val packageRegex = Regex("package\\s+([a-zA-Z0-9_.]+);")
    private val extLength = ".java".length

    companion object {
        val logger = org.slf4j.LoggerFactory.getLogger(JavaWorker::class.java)
    }

    override fun addJob(job: InstructionFileJob) {
        this.jobs.add(job)
        tryAddClassToTree(job.code, job)

        // since the Java Analyser imports will be in data structures
        val container = JavaAnalyser().analysis(job.code, job.fileSummary.location)
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

    private fun tryAddClassToTree(code: String, job: InstructionFileJob) {
        val packageMatch = packageRegex.find(code)
        if (packageMatch != null) {
            val packageName = packageMatch.groupValues[1]
            // in Java the filename is the class name
            val className = job.fileSummary.filename.substring(0, job.fileSummary.filename.length - extLength)
            val fullClassName = "$packageName.$className"
            fileTree[fullClassName] = job
        }
    }

    override suspend fun start(): Collection<Instruction> = coroutineScope {
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

        val lists = jobs.map { job ->
            val jobContext =
                JobContext(
                    job,
                    context.qualityTypes,
                    fileTree,
                    context.builderConfig,
                    context.completionTypes,
                    context.maxCompletionInOneFile
                )

            context.codeContextStrategies.map { type ->
                val codeStrategyBuilder = type.builder(jobContext)
                codeStrategyBuilder.build()
            }.flatten()
        }.flatten()

        // take context.completionTypeSize for each type
        val finalList: EnumMap<CompletionBuilderType, List<TypedCompletionIns>> =
            EnumMap(CompletionBuilderType::class.java)

        val instructions: MutableList<Instruction> = mutableListOf()

        lists.map {
            finalList[it.type] = finalList[it.type]?.plus(it) ?: listOf(it)
        }

        val result = finalList.values.map { it.take(context.completionTypeSize) }.flatten()
        result.map {
            instructions.add(it.unique())
            outputFile.appendText(it.toString() + "\n")
        }

        return@coroutineScope instructions
    }
}
