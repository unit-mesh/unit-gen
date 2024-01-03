package cc.unitmesh.pick.worker.lang

import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.ext.buildSourceCode
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.base.LangWorker
import chapi.ast.javaast.JavaAnalyser
import org.slf4j.Logger

/**
 * The JavaWorker class is an implementation of the LangWorker interface.
 * It provides functionality for handling Java based instruction file jobs.
 */
open class JavaWorker(override val workerContext: WorkerContext) : LangWorker {
    override val jobs: MutableList<InstructionFileJob> = mutableListOf()
    override val fileTree: HashMap<String, InstructionFileJob> = hashMapOf()
    override val logger: Logger = org.slf4j.LoggerFactory.getLogger(JavaWorker::class.java)

    protected open val packageRegex = Regex("package\\s+([a-zA-Z0-9_.]+);")
    protected open val extLength = ".java".length

    /**
     * Adds a job to the list of instruction file jobs.
     *
     * @param job The InstructionFileJob object to be added.
     */
    override fun prepareJob(job: InstructionFileJob) {
        this.jobs.add(job)

        try {
            tryAddClassToTree(job)
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

    /**
     * Tries to add a class to the file tree.
     *
     * This method takes an InstructionFileJob object as a parameter and attempts to add the class represented by the job to the file tree.
     * It extracts the package name from the code of the job, and if a package name is found, it constructs the full class name using the package name and the filename of the job.
     * The method then adds the full class name as a key to the file tree, with the job as its corresponding value.
     *
     * @param job the InstructionFileJob object representing the class to be added to the file tree
     */
    override fun tryAddClassToTree(job: InstructionFileJob) {
        val packageMatch = packageRegex.find(job.code)
        if (packageMatch != null) {
            val packageName = packageMatch.groupValues[1]
            // in Java the filename is the class name
            val className = job.fileSummary.filename.substring(0, job.fileSummary.filename.length - extLength)
            val fullClassName = "$packageName.$className"
            fileTree[fullClassName] = job
        }
    }
}
