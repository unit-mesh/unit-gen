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

    /**
     *
     * 对[InstructionFileJob]进行预处理，主要包括以下几个步骤：
     * 1. 添加到fileTree中，方便后续查找
     * 2. 解析代码，生成抽象语法树
     *
     * Note: 在 Chapi 中，Kotlin 的解析模式分为两种，一种是 Full，一种是 Basic，Full 模式会解析出更多的信息，但是会消耗更多的时间。
     * 在测试代码生成的过程中，我们只需要解析出基本的信息即可，所以使用 FULL 模式即可。
     *
     * @param job 待解析的文件
     * @return 返回解析后的文件
     */
    override fun prepareJob(job: InstructionFileJob) {
        this.jobs.add(job)

        try {
            tryAddClassToTree(job)
            val container = if (context.completionTypes.contains(CompletionBuilderType.TEST_CODE_GEN)) {
                KotlinAnalyser().analysis(job.code, job.fileSummary.location, ParseMode.Full)
            } else {
                KotlinAnalyser().analysis(job.code, job.fileSummary.location)
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