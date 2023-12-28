package cc.unitmesh.pick.worker.lang

import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.ext.buildSourceCode
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.base.LangWorker
import cc.unitmesh.pick.worker.job.InstructionFileJob
import chapi.ast.kotlinast.KotlinAnalyser
import chapi.parser.ParseMode
import org.slf4j.Logger

class KotlinWorker(override val context: WorkerContext) : JavaWorker(context), LangWorker {
    override val jobs: MutableList<InstructionFileJob> = mutableListOf()

    override val fileTree: HashMap<String, InstructionFileJob> = hashMapOf()
    override val logger: Logger = org.slf4j.LoggerFactory.getLogger(KotlinWorker::class.java)

    override val packageRegex = Regex("package\\s+([a-zA-Z0-9_.]+)")
    override val extLength = ".kt".length

    override fun prepareJob(job: InstructionFileJob) {
        this.jobs.add(job)

        try {
            tryAddClassToTree(job)
            // since the Java Analyser imports will be in data structures
            val container = if (context.completionTypes.contains(CompletionBuilderType.TEST_CODE_GEN)) {
                KotlinAnalyser().analysis(job.code, job.fileSummary.location)
            } else {
                KotlinAnalyser().analysis(job.code, job.fileSummary.location, ParseMode.Full)
            }

            job.codeLines = job.code.lines()
            container.buildSourceCode(job.codeLines)

            job.container = container
        } catch (e: Exception) {
            logger.error("failed to prepare job: ${job.fileSummary.location}")
            e.printStackTrace()
        }
    }
}