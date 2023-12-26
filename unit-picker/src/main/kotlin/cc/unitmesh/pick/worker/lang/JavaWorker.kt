package cc.unitmesh.pick.worker.lang

import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.Instruction
import cc.unitmesh.pick.worker.job.JobContext
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.ext.buildSourceCode
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.base.LangWorker
import cc.unitmesh.pick.project.ProjectContext
import chapi.ast.javaast.JavaAnalyser
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
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
class JavaWorker(override val context: WorkerContext) : LangWorker {
    override val jobs: MutableList<InstructionFileJob> = mutableListOf()
    override val fileTree: HashMap<String, InstructionFileJob> = hashMapOf()
    override val logger: Logger = org.slf4j.LoggerFactory.getLogger(JavaWorker::class.java)

    private val packageRegex = Regex("package\\s+([a-zA-Z0-9_.]+);")
    private val extLength = ".java".length

    /**
     * Adds a job to the list of instruction file jobs.
     *
     * @param job The InstructionFileJob object to be added.
     */
    override fun prepareJob(job: InstructionFileJob) {
        this.jobs.add(job)

        try{
            tryAddClassToTree(job.code, job)
            // since the Java Analyser imports will be in data structures
            val container = JavaAnalyser().analysis(job.code, job.fileSummary.location)
            job.codeLines = job.code.lines()
            container.buildSourceCode(job.codeLines)

            job.container = container
        } catch (e: Exception) {
            logger.error("failed to prepare job: ${job.fileSummary.location}")
            e.printStackTrace()
        }
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
}
