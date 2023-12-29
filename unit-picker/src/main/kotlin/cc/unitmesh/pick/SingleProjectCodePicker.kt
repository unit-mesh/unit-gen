package cc.unitmesh.pick

import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.option.InsPickerOption
import cc.unitmesh.core.Instruction
import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.ext.GitUtil
import cc.unitmesh.pick.ext.PickDirectoryWalker
import cc.unitmesh.pick.project.ProjectContext
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.WorkerManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.archguard.scanner.analyser.count.FileJob
import org.archguard.scanner.analyser.count.LanguageWorker
import java.nio.file.Files
import java.nio.file.Path

/**
 * The `SimpleCodePicker` class is responsible for executing the `PickerOption` configuration and obtaining a list of
 * instructions from processing a code directory. It implements the `CodePicker` interface.
 *
 * @property config The picker configuration options.
 *
 * @constructor Creates a `SimpleCodePicker` instance with the provided configuration.
 */
class SingleProjectCodePicker(private val config: InsPickerOption) {
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    /**
     * Executes the code picker with the provided configuration.
     *
     * This method performs the following steps:
     *
     * 1. Checks out the code from the specified URL, branch, and base directory using the `checkoutCode` function.
     * 2. Initializes the necessary instances for code processing, such as `LanguageWorker` and `WorkerManager`.
     * 3. Creates a channel to handle file jobs and initializes an empty list for storing instructions.
     * 4. Starts two coroutines to process the code directory:
     *    - The first coroutine recursively walks through the code directory using the `PickDirectoryWalker` class and
     *      sends each file job to the file job channel.
     *    - The second coroutine processes each file job received from the channel using the `languageWorker`,
     *      generating instructions and adds them to the `workerManager`.
     * 5. Waits for both coroutines to finish.
     * 6. Returns the list of instructions stored in the `summary` variable.
     *
     * Usage:
     *
     * ```kotlin
     * val codePicker = SingleProjectCodePicker(url = "https://example.com/repository.git")
     * val instructions = codePicker.execute()
     * ```
     *
     * @return The list of instructions obtained from processing the code directory.
     * @throws InterruptedException if the execution is interrupted.
     * @throws IOException if an I/O error occurs during code checkout or processing.
     */
    suspend fun execute() = coroutineScope {
        val tempGitDir = Path.of(config.baseDir, ".tmp")
        if (!tempGitDir.toFile().exists()) {
            Files.createDirectories(tempGitDir)
        }

        val codeDir = GitUtil
            .checkoutCode(config.url, config.branch, tempGitDir, config.gitDepth)
            .toFile().canonicalFile

        logger.info("start walk $codeDir")

        val languageWorker = LanguageWorker()
        val language = SupportedLang.from(config.language.lowercase())
            ?: throw IllegalArgumentException("unsupported language: ${config.language}")

        val workerManager = WorkerManager(
            WorkerContext(
                config.codeStrategyTypes,
                config.codeQualityTypes,
                config.insOutputConfig,
                pureDataFileName = config.pureDataFileName(),
                config.completionTypes,
                config.maxCompletionEachFile,
                config.completionTypeSize,
                qualityThreshold = InsQualityThreshold(
                    complexity = InsQualityThreshold.MAX_COMPLEXITY,
                    fileSize = InsQualityThreshold.MAX_FILE_SIZE,
                    maxLineInCode = config.maxLineInCode,
                    maxCharInCode = config.maxCharInCode,
                    maxTokenLength = config.maxTokenLength,
                ),
                project = ProjectContext(
                    language = language
                )
            )
        )


        workerManager.init(codeDir, language)

        val walkdirChannel = Channel<FileJob>()
        val summary = mutableListOf<Instruction>()

        val canRemoveComment = !config.completionTypes.contains(CompletionBuilderType.DOCUMENTATION)

        launch {
            launch {
                PickDirectoryWalker(walkdirChannel).start(codeDir.toString())
                walkdirChannel.close()
            }
            launch {
                for (fileJob in walkdirChannel) {
                    languageWorker.processFile(fileJob)?.let {
                        workerManager.tryAddJobByThreshold(InstructionFileJob.from(it, canRemoveComment), language)
                    }
                }

                summary.addAll(workerManager.runAll())
            }

        }.join()

        logger.info("finish walk $codeDir")

        return@coroutineScope summary
    }
}

