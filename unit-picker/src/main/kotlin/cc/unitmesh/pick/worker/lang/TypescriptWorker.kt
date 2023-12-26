package cc.unitmesh.pick.worker.lang

import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.base.LangWorker
import cc.unitmesh.pick.worker.job.InstructionFileJob
import chapi.ast.typescriptast.TypeScriptAnalyser
import org.slf4j.Logger

class TypescriptWorker(override val context: WorkerContext) : LangWorker {
    override val jobs: MutableList<InstructionFileJob> = mutableListOf()
    override val fileTree: HashMap<String, InstructionFileJob> = hashMapOf()
    override val logger: Logger = org.slf4j.LoggerFactory.getLogger(TypescriptWorker::class.java)

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
}