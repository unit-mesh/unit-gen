package cc.unitmesh.pick.related

import cc.unitmesh.core.intelli.SimilarChunkContext
import cc.unitmesh.core.intelli.SimilarChunker
import cc.unitmesh.pick.config.InstructionFileJob

/**
 * This class is used to calculate the similar chunks of Java code.
 * With different strategies (recent file strategy and chunk strategy), this class can find and analyze similar chunks
 * of code.
 *
 * This class extends the SimilarChunksWithPaths class, providing additional functionality for handling Java code.
 */
class JavaSimilarChunker(private val fileTree: HashMap<String, InstructionFileJob>) : SimilarChunker() {
    override fun calculate(text: String, canonicalName: String): SimilarChunkContext {
        val lines = text.split("\n")
        // take lines before the cursor which will select from the last line
        val beforeCursor = lines.takeLast(snippetLength).joinToString("\n")

        val canonicalNames = fileTree.keys.toList()
        val relatedCodePath = packageNameLevelJaccardSimilarity(canonicalNames, canonicalName)
            .toList()
            .sortedByDescending { it.first }
            .take(maxRelevantFiles)
            .map { it.second }

        val chunks = chunkedCode(beforeCursor)
        val allRelatedChunks = relatedCodePath
            .mapNotNull { fileTree[it] }
            .map { chunkedCode(it.code) }
            .flatten()

        val similarChunks = allRelatedChunks.map {
            val score = similarityScore(tokenize(it).toSet(), chunks.toSet())
            score to it
        }.sortedByDescending { it.first }
            .take(maxRelevantFiles)
            .map { it.second }

        return SimilarChunkContext("java", relatedCodePath, similarChunks.take(3))
    }

    private fun packageNameLevelJaccardSimilarity(chunks: List<String>, text: String): Map<Double, String> {
        val packageName = packageNameTokenize(text)
        return chunks.map { chunk ->
            val chunkPackageName = packageNameTokenize(chunk)
            val score = similarityScore(packageName.toSet(), chunkPackageName.toSet())
            score to chunk
        }.toMap()
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