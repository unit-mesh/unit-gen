package cc.unitmesh.pick.worker

import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.picker.PickJob
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
class JavaLangWorker : LangWorker() {
    private val jobs: MutableList<PickJob> = mutableListOf()
    private val packageTree: MutableMap<String, PickJob> = mutableMapOf()

    private val packageRegex = Regex("package\\s+([a-zA-Z0-9_.]+);")
    private val extLength = ".java".length

    override fun addJob(job: PickJob) {
        this.jobs.add(job)
        tryAddClassToTree(job.code, job)
        job.container = JavaAnalyser().analysis(job.code, job.fileSummary.location)
    }

    private fun tryAddClassToTree(code: String, job: PickJob) {
        val packageMatch = packageRegex.find(code)
        if (packageMatch != null) {
            val packageName = packageMatch.groupValues[1]
            // in Java the filename is the class name
            val className = job.fileSummary.filename.substring(0, job.fileSummary.filename.length - extLength)
            val fullClassName = "$packageName.$className"
            packageTree[fullClassName] = job
        }
    }

    override suspend fun start(): List<InstructionBuilder> = coroutineScope {
        jobs.map {

        }

        return@coroutineScope listOf()
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
