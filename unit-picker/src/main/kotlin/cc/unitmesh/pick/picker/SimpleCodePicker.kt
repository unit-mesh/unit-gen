package cc.unitmesh.pick.picker

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.walker.PickDirectoryWalker
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

interface CodePicker

class SimpleCodePicker(private val config: PickerConfig) : CodePicker {
    /**
     * Executes the code picker with the provided configuration.
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
        val codeDir = checkoutCode(this@SimpleCodePicker, config.url, config.branch, config.baseDir)
            .toFile().canonicalFile

        logger.info("start picker")

        val languageWorker = LanguageWorker()
        val workerManager = WorkerManager(WorkerContext(config.builderTypes))
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
                        workerManager.addJob(InstructionJob.from(it))
                    }
                }

                summary.addAll(workerManager.runAll())
            }

        }.join()

        return@coroutineScope summary
    }

    private fun moveRepository(sourceDir: Path, targetDir: Path) {
        try {
            Files.move(sourceDir, targetDir)
        } catch (e: Exception) {
            // Handle the exception appropriately (e.g., log or throw)
            e.printStackTrace()
        }
    }

    companion object {
        private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

        private val gitUrlRegex =
            """(git@|http://|https://)((?<host>[\w\.@]+)(/|:))(?<owner>[\w,\-,\_]+)/(?<repo>[\w,\-,\_]+)(.git){0,1}((/){0,1})""".toRegex()

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
            val matchResult = gitUrlRegex.find(trimmedUrl) ?: throw IllegalArgumentException("invalid git url: $url")

            val host = matchResult.groups["host"]!!.value
            val owner = matchResult.groups["owner"]!!.value
            val repo = matchResult.groups["repo"]!!.value

            return "$host/$owner/$repo"
        }

        fun checkoutCode(simpleCodePicker: SimpleCodePicker, url: String, branch: String, baseDir: String): Path {
            if (!gitUrlRegex.matches(url)) {
                return Path.of(url)
            }

            val gitDir = gitUrlToPath(url)
            val targetDir = Path.of(baseDir, gitDir)
            logger.info("targetDir: $targetDir")
            if (targetDir.toFile().exists()) {
                logger.info("targetDir exists: $targetDir")
                return targetDir
            }

            val settings = GitSourceSettings.fromArgs(arrayOf("--repository", url, "--branch", branch))
            executeGitCheckout(settings)

            // mv settings.repository to targetDir
            val sourceRepoDir = Path.of(settings.repositoryPath)
            try {
                Files.createDirectories(targetDir.parent)
            } catch (e: Exception) {
                logger.info("create dir failed: ${targetDir.parent}")
            }

            logger.info("targetDir: $targetDir")
            simpleCodePicker.moveRepository(sourceRepoDir, targetDir)
            return targetDir
        }
    }
}
