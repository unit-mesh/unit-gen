package cc.unitmesh.pick

import cc.unitmesh.pick.builder.InstructionFileJob
import cc.unitmesh.pick.builder.PickerOption
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.walker.PickDirectoryWalker
import cc.unitmesh.pick.threshold.QualityThreshold
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.WorkerManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.archguard.action.checkout.GitSourceSettings
import org.archguard.action.checkout.executeGitCheckout
import org.archguard.scanner.analyser.count.FileJob
import org.archguard.scanner.analyser.count.LanguageWorker
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

interface CodePicker

/**
 * The `SimpleCodePicker` class is responsible for executing the `PickerOption` configuration and obtaining a list of
 * instructions from processing a code directory. It implements the `CodePicker` interface.
 *
 * @property config The picker configuration options.
 *
 * @constructor Creates a `SimpleCodePicker` instance with the provided configuration.
 */
class SimpleCodePicker(private val config: PickerOption) : CodePicker {
    /**
     * Executes the code config with the provided configuration.
     *
     * This method performs the following steps:
     *
     * 1. Checks out the code from the specified URL, branch, and base directory using the `checkoutCode` function.
     * 2. Creates an instance of the `LanguageWorker` class.
     * 3. Creates an instance of the `WorkerManager` class and initializes it with the provided builder types.
     * 4. Creates a channel for file jobs.
     * 5. Initializes an empty list for storing instructions.
     * 6. Starts two coroutines to process the code directory:
     *    - The first coroutine uses the `PickDirectoryWalker` class to recursively walk through the code directory and
     *      sends each file job to the file job channel.
     *    - The second coroutine processes each file job received from the channel using the `languageWorker` and adds
     *      the resulting instruction to the `workerManager`.
     * 7. Waits for both coroutines to finish.
     * 8. Returns the list of instructions stored in the `summary` variable.
     *
     * Usage:
     *
     * ```kotlin
     * val codePicker = CodePicker()
     * codePicker.config.url = "https://example.com/repository.git"
     * codePicker.config.branch = "main"
     * codePicker.config.baseDir = "/path/to/code"
     * val instructions = codePicker.execute()
     * ```
     *
     * @return The list of instructions obtained from processing the code directory.
     *
     * @throws InterruptedException if the execution is interrupted.
     * @throws IOException if an I/O error occurs during code checkout or processing.
     */
    suspend fun execute() = coroutineScope {
        val tempGitDir = Path.of(config.baseDir, ".tmp")
        if (!tempGitDir.toFile().exists()) {
            Files.createDirectories(tempGitDir)
        }

        val codeDir = checkoutCode(config.url, config.branch, tempGitDir, config.gitDepth)
            .toFile().canonicalFile

        logger.info("start config")

        val languageWorker = LanguageWorker()
        val workerManager = WorkerManager(
            WorkerContext(
                config.codeContextStrategies,
                config.codeQualityTypes,
                config.builderConfig,
                pureDataFileName = config.pureDataFileName(),
                config.completionTypes,
                config.maxCompletionInOneFile,
                config.completionTypeSize,
                qualityThreshold = QualityThreshold(
                    complexity = QualityThreshold.MAX_COMPLEXITY,
                    fileSize = QualityThreshold.MAX_FILE_SIZE,
                    maxLineInCode = config.maxLineInCode,
                    maxCharInCode = config.maxCharInCode,
                )
            )
        )
        val walkdirChannel = Channel<FileJob>()
        val summary = mutableListOf<Instruction>()

        launch {
            launch {
                PickDirectoryWalker(walkdirChannel).start(codeDir.toString())
                walkdirChannel.close()
            }
            launch {
                for (fileJob in walkdirChannel) {
                    languageWorker.processFile(fileJob)?.let {
                        workerManager.addJobByThreshold(InstructionFileJob.from(it))
                    }
                }

                summary.addAll(workerManager.runAll())
            }

        }.join()

        return@coroutineScope summary
    }

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

        private val gitUrlRegex =
            """(git@|http://|https://)((?<host>[\w\.@]+)(/|:))(?<owner>[\w,\-\_]+)/(?<repo>[\w,\-,._]+)"""
                .toRegex()

        /**
         * Convert git url to path
         *
         * for example:
         *
         * - `https://github.com/unit-mesh/unit-pick` tobe `github.com/unit-mesh/unit-pick`
         * - `git://github.com/unit-mesh/unit-pick` tobe `github.com/unit-mesh/unit-pick`
         * - `git://github.com/unit-mesh/unit-pick.git` tobe `github.com/unit-mesh/unit-pick`
         * - `http://github.com/unit-mesh/unit-pick` tobe `github.com/unit-mesh/unit-pick`
         *
         */
        fun gitUrlToPath(url: String): String {
            val trimmedUrl = url.trim()
                .removeSuffix("/")
                .removeSuffix(".git")

            val matchResult = gitUrlRegex.find(trimmedUrl) ?: throw IllegalArgumentException("invalid git url: $url")

            val host = matchResult.groups["host"]!!.value
            val owner = matchResult.groups["owner"]!!.value
            val repo = matchResult.groups["repo"]!!.value

            return "$host/$owner/$repo"
        }

        fun checkoutCode(url: String, branch: String, baseDir: Path, depth: Int): Path {
            if (!gitUrlRegex.matches(url)) {
                return Path.of(url)
            }

            val gitDir = gitUrlToPath(url)
            val targetDir = baseDir.resolve(gitDir)
            logger.info("targetDir: $targetDir")

            if (targetDir.toFile().exists()) {
                logger.info("targetDir exists: $targetDir")
                return targetDir
            }

            val settings = GitSourceSettings(
                repository = url,
                branch = branch,
                workdir = targetDir.parent.absolutePathString(),
                fetchDepth = depth
            )

            try {
                Files.createDirectories(targetDir.parent)
            } catch (e: Exception) {
                logger.info("create dir failed: ${targetDir.parent}")
            }

            executeGitCheckout(settings)
            return targetDir
        }
    }
}
