package cc.unitmesh.pick.ext

import org.archguard.action.checkout.GitSourceSettings
import org.archguard.action.checkout.executeGitCheckout
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

object GitUtil {
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)
    private val GIT_URL_REGEX =
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

        val matchResult = GIT_URL_REGEX.find(trimmedUrl) ?: throw IllegalArgumentException("invalid git url: $url")

        val host = matchResult.groups["host"]!!.value
        val owner = matchResult.groups["owner"]!!.value
        val repo = matchResult.groups["repo"]!!.value

        return "$host/$owner/$repo"
    }

    fun checkoutCode(url: String, branch: String, baseDir: Path, depth: Int): Path {
        if (!GIT_URL_REGEX.matches(url)) {
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