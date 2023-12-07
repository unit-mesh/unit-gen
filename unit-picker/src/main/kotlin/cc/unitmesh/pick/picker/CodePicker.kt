package cc.unitmesh.pick.picker

import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.worker.WorkerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.archguard.action.checkout.GitSourceSettings
import org.archguard.action.checkout.executeGitCheckout
import org.archguard.scanner.analyser.count.FileJob
import org.archguard.scanner.analyser.count.LanguageWorker
import java.nio.file.Files
import java.nio.file.Path

class CodePicker(private val config: PickerConfig) {
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    fun execute() = runBlocking {
        val scope = CoroutineScope(coroutineContext)
        scope.launch {
            val codeDir = checkoutCode(config.url, config.branch, config.baseDir)
                .toFile().canonicalFile

            logger.info("start picker")

            val languageWorker = LanguageWorker()
            val workerManager = WorkerManager()
            val walkdirChannel = Channel<FileJob>()
            val summary = mutableListOf<InstructionBuilder>()

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

            logger.info("stop picker")


            // 3. generate tree to jsonl
        }
    }

    fun checkoutCode(url: String, branch: String, baseDir: String): Path {
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
        moveRepository(sourceRepoDir, targetDir)
        return targetDir
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
        val gitUrlRegex =
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
    }
}

