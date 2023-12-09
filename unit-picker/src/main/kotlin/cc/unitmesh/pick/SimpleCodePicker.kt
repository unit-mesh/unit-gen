package cc.unitmesh.pick

import cc.unitmesh.pick.config.InstructionFileJob
import cc.unitmesh.pick.config.PickerOption
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.walker.PickDirectoryWalker
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.WorkerManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.archguard.action.checkout.GitSourceSettings
import org.archguard.action.checkout.executeGitCheckout
import org.archguard.scanner.analyser.count.FileJob
import org.archguard.scanner.analyser.count.LanguageWorker
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

interface CodePicker

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

        val codeDir = checkoutCode(this@SimpleCodePicker, config.url, config.branch, tempGitDir)
            .toFile().canonicalFile

        logger.info("start config")

        val languageWorker = LanguageWorker()
        val workerManager = WorkerManager(WorkerContext(
            config.builderTypes,
            config.codeQualityTypes,
            config.builderConfig,
            pureDataFileName = config.pureDataFileName(),
        ))
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
                        workerManager.addJob(InstructionFileJob.from(it))
                    }
                }

                summary.addAll(workerManager.runAll())
            }

        }.join()

        return@coroutineScope summary
    }

    fun blockingExecute() = runBlocking {
        return@runBlocking execute()
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

        fun checkoutCode(simpleCodePicker: SimpleCodePicker, url: String, branch: String, baseDir: Path): Path {
            if (!gitUrlRegex.matches(url)) {
                return Path.of(url)
            }

            val gitDir = gitUrlToPath(url)
            val targetDir = baseDir.resolve(gitDir)
            logger.info("targetDir: $targetDir")
            if (targetDir.toFile().exists()) {
                logger.info("targetDir exists: $targetDir")
                // todo: if exists pull rebase code
                return targetDir
            }

            val settings = GitSourceSettings(
                repository = url,
                branch = branch,
                workdir = baseDir.absolutePathString(),
            )
            val sourceRepoDir = baseDir.resolve(settings.repositoryPath)
            try {
                Files.createDirectories(targetDir.parent)
            } catch (e: Exception) {
                logger.info("create dir failed: ${targetDir.parent}")
            }

            executeGitCheckout(settings)

            logger.info("targetDir: $targetDir")
            simpleCodePicker.moveRepository(sourceRepoDir, targetDir)
            return targetDir
        }
    }
}
