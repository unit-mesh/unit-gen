package cc.unitmesh.pick.worker.lang

import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.base.LangWorker
import cc.unitmesh.pick.worker.job.InstructionFileJob
import chapi.ast.rustast.RustAnalyser
import org.slf4j.Logger

class RustWorker(override val workerContext: WorkerContext) : LangWorker {
    override val jobs: MutableList<InstructionFileJob> = mutableListOf()
    override val fileTree: HashMap<String, InstructionFileJob> = hashMapOf()
    override val logger: Logger = org.slf4j.LoggerFactory.getLogger(RustWorker::class.java)

    override fun prepareJob(job: InstructionFileJob) {
        this.jobs.add(job)
        try {
            println("RustWorker prepareJob: ${job.fileSummary.location}")
            val container = RustAnalyser().analysis(job.code, job.fileSummary.location)
            job.codeLines = job.code.lines()
            container.DataStructures.map { ds ->
                ds.Imports = container.Imports

                ds.Content = CodeDataStructUtil.contentByPosition(job.codeLines, ds.Position)
                ds.Functions.map {
                    it.Content = CodeDataStructUtil.contentByPosition(job.codeLines, it.Position)
                }
            }

            job.container = container
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}