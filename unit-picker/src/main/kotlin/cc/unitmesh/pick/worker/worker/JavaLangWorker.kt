package cc.unitmesh.pick.worker.worker

import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.config.SingleFileInstructionJob
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.worker.LangWorker
import cc.unitmesh.pick.worker.WorkerContext
import chapi.ast.javaast.JavaAnalyser
import kotlinx.coroutines.coroutineScope
import org.archguard.scanner.analyser.count.FileJob

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
class JavaLangWorker(val context: WorkerContext) : LangWorker() {
    private val jobs: MutableList<SingleFileInstructionJob> = mutableListOf()
    private val fileTree: HashMap<String, SingleFileInstructionJob> = hashMapOf()

    private val packageRegex = Regex("package\\s+([a-zA-Z0-9_.]+);")
    private val extLength = ".java".length

    override fun addJob(job: SingleFileInstructionJob) {
        this.jobs.add(job)
        tryAddClassToTree(job.code, job)

        // since the Java Analyser imports will be in data structures
        val container = JavaAnalyser().analysis(job.code, job.fileSummary.location)
        job.codeLines = job.code.lines()
        container.DataStructures.map { ds ->
            ds.Imports = container.Imports

            ds.Content = CodeDataStructUtil.contentByPosition(job.codeLines, ds.Position)
            ds.Functions.map { it.apply { it.Content = CodeDataStructUtil.contentByPosition(job.codeLines, it.Position) } }
        }

        job.container = container
    }

    private fun tryAddClassToTree(code: String, job: SingleFileInstructionJob) {
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
        return@coroutineScope jobs.map {
            InstructionBuilder.build(
                context.builderTypes, context.qualityTypes, fileTree, it, context.builderConfig
            )
        }.flatten()
    }

    // check by history?
    suspend fun startWithHistory(filePath: String) = coroutineScope {
        // 1. read directory to a collection of files for FileJob

        // 2. check package information from line 1?

        // 3. build full project trees

        // 4. check history
    }

    // split methods with comments
    fun splitMethods(fileJob: FileJob): List<FileJob> {
        // TODO: split methods with comments
        return listOf()
    }
}
