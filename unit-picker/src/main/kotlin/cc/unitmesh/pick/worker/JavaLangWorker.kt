package cc.unitmesh.pick.worker

import cc.unitmesh.pick.picker.PickJob
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
    val jobs: MutableList<PickJob> = mutableListOf()
    val packageTree: MutableMap<String, PickJob> = mutableMapOf()

    val packageRegex = Regex("package\\s+([a-zA-Z0-9_\\.]+);")
    val extLength = ".java".length

    override fun addJob(job: PickJob) {
        this.jobs.add(job)
        val packageMatch = packageRegex.find(job.content.decodeToString())
        if (packageMatch != null) {
            val packageName = packageMatch.groupValues[1]
            val className = job.filename.substring(0, job.filename.length - extLength)
            val fullClassName = "$packageName.$className"
            packageTree[fullClassName] = job
        }
    }

    override suspend fun start() = coroutineScope {
        // 1. read directory to a collection of files for FileJob

        // 2. check package information from line 1?

        // 3. build full project trees
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
