package cc.unitmesh.pick.related

import cc.unitmesh.core.intelli.SimilarChunkContext
import cc.unitmesh.core.intelli.SimilarChunksWithPaths
import cc.unitmesh.pick.config.InstructionFileJob

/**
 * This class is used to calculate the similar chunks of Java code.
 * With different strategies (recent file strategy and chunk strategy), this class can find and analyze similar chunks
 * of code.
 *
 * This class extends the SimilarChunksWithPaths class, providing additional functionality for handling Java code.
 */
class JavaSimilarChunks(val fileTree: HashMap<String, InstructionFileJob>) : SimilarChunksWithPaths() {
    override fun calculate(text: String): SimilarChunkContext {
        TODO("Not yet implemented")
    }

    /**
     * Calculates the common path from a list of package names.
     *
     * @param paths A list of package names as strings.
     * @return The common path shared by all the package names.
     *
     * Examples:
     * - For the input ["org.unitmesh.controller.BlogController", "org.unitmesh.service.BlogService", "org.unitmesh.model.BlogModel"],
     *   the method will return "org.unitmesh".
     * - For an empty input list, the method will return an empty string.
     * - If the input list contains only one element, the method will return that element.
     * - If the input list contains package names with different common paths, the method will return an empty string.
     *
     * Note: The method assumes that the input list contains valid package names and that the delimiter between segments
     * within a package name is a dot (".").
     */
    fun calculateCommonPath(paths: List<String>): String {
        if (paths.isEmpty()) return ""

        val commonPathSegments = paths
            .map { it.split(".") }
            .reduce { acc, packageSegments ->
                acc.zip(packageSegments)
                    .takeWhile { (seg1, seg2) -> seg1 == seg2 }
                    .map { (seg1, _) -> seg1 }
            }

        return commonPathSegments.joinToString(".")
    }

    fun chunkedCode(code: String): List<String> {
        return code
            .split("\n", limit = snippetLength)
            .filter {
                val trim = it.trim()
                !(trim.startsWith("import ") || trim.startsWith("package "))
            }
            .chunked(snippetLength)
            .flatten()
    }
}