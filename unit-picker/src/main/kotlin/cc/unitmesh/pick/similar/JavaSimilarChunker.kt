package cc.unitmesh.pick.similar

import cc.unitmesh.core.intelli.SimilarChunkContext
import cc.unitmesh.core.intelli.SimilarChunker
import cc.unitmesh.pick.builder.InstructionFileJob
import org.slf4j.Logger

/**
 * This class is used to calculate the similar chunks of Java code.
 * With different strategies (recent file prompt and chunk prompt), this class can find and analyze similar chunks
 * of code.
 *
 * This class extends the SimilarChunksWithPaths class, providing additional functionality for handling Java code.
 */
class JavaSimilarChunker(private val fileTree: HashMap<String, InstructionFileJob>) : SimilarChunker() {
    companion object {
        val logger: Logger = org.slf4j.LoggerFactory.getLogger(JavaSimilarChunker::class.java)
    }

    /**
     * Calculates the similar code chunks based on the given text and canonical name.
     *
     * @param text The text in which the code chunks are present.
     * @param canonicalName The canonical name of the code snippet.
     * @return A SimilarChunkContext object containing information about similar code paths and similar chunks of code.
     */
    override fun calculate(text: String, canonicalName: String): SimilarChunkContext {
        val lines = text.split("\n")
        // take lines before the cursor which will select from the last line
        val beforeCursor = lines.takeLast(snippetLength).joinToString("\n")

        val canonicalNames = fileTree.keys.filter { it != canonicalName }
        val relatedCodePath = packageNameLevelJaccardSimilarity(canonicalNames, canonicalName)
            .toList()
            .sortedByDescending { it.first }
            .take(maxRelevantFiles)
            .map { it.second }

        logger.info("canonicalName: $canonicalName, relatedCodePath: $relatedCodePath")

        val chunks = chunkedCode(beforeCursor)
        val allRelatedChunks = relatedCodePath
            .mapNotNull { fileTree[it] }
            .map { chunkedCode(it.code).joinToString("\n") }

        val similarChunks = allRelatedChunks.map {
            val score = similarityScore(tokenize(it).toSet(), chunks.toSet())
            score to it
        }.sortedByDescending { it.first }
            .take(maxRelevantFiles)
            .map { it.second }

        // take the first 3 similar chunks or empty
        val similarChunksText = if (similarChunks.size > 3) {
            similarChunks.take(3)
        } else {
            similarChunks
        }.map {
            // clean up the similar chunks, if start with multiple \n, remove to one
            it.replace(Regex("^\\n+"), "\n")
        }

        return SimilarChunkContext("java", relatedCodePath, similarChunksText)
    }

    private fun packageNameLevelJaccardSimilarity(chunks: List<String>, text: String): Map<Double, String> {
        val packageName = packageNameTokenize(text)
        return chunks.mapNotNull { chunk ->
            val chunkPackageName = packageNameTokenize(chunk)
            val score = similarityScore(packageName.toSet(), chunkPackageName.toSet())
            if (score >= scoreThreshold) {
                score to chunk
            } else {
                null
            }
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

    private fun chunkedCode(code: String): List<String> {
        return code
            .split("\n")
            .filter {
                val trim = it.trim()
                !(trim.startsWith("import ") || trim.startsWith("package ") || trim == "\n")
            }
            .chunked(snippetLength)
            .flatten()
    }
}