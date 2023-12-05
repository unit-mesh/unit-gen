package cc.unitmesh.eval.picker

import org.archguard.action.checkout.GitSourceSettings
import org.archguard.action.checkout.executeGitCheckout
import org.jetbrains.annotations.TestOnly
import java.nio.file.Path

class CodePicker(val config: PickerConfig) {

    fun run() {
        // 1. check config.url is a valid url or path
        checkoutCode(config.url, config.branch, config.baseDir)

        // 2. select files to tree

        // 3. generate tree to jsonl
    }

    @TestOnly
    fun checkoutCode(url: String, branch: String, baseDir: String) {
        if (!gitUrlRegex.matches(url)) {
            return
        }

        val gitDir = gitUrlToPath(url)
        val targetDir = Path.of(baseDir, gitDir)
        if (targetDir.toFile().exists()) {
            return
        }

        val settings = GitSourceSettings.fromArgs(arrayOf("--repository", url, "--branch", branch))
        executeGitCheckout(settings)

        // mv settings.repository to targetDir
        targetDir.toFile().mkdirs()
        val repositoryPath = Path.of(settings.repository)
        repositoryPath.toFile().renameTo(targetDir.toFile())
    }

    companion object {
        val gitUrlRegex =
            """(git@|http://|https://)((?<host>[\w\.@]+)(/|:))(?<owner>[\w,\-,\_]+)/(?<repo>[\w,\-,\_]+)(.git){0,1}((/){0,1})""".toRegex()

        /**
         * Convert git url to path
         *
         * for example:
         *
         * - `https://github.com/unit-mesh/unit-eval` tobe `github.com/unit-mesh/unit-eval`
         * - `git://github.com/unit-mesh/unit-eval` tobe `github.com/unit-mesh/unit-eval`
         * - `git://github.com/unit-mesh/unit-eval.git` tobe `github.com/unit-mesh/unit-eval`
         * - `http://github.com/unit-mesh/unit-eval` tobe `github.com/unit-mesh/unit-eval`
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
