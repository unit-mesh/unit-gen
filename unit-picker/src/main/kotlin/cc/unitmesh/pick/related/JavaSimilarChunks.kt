package cc.unitmesh.pick.related

import cc.unitmesh.core.intelli.SimilarChunkContext
import cc.unitmesh.core.intelli.SimilarChunksWithPaths

/**
 * This class is used to calculate the similar chunks of Java code.
 * With different strategies (recent file strategy and chunk strategy), this class can find and analyze similar chunks
 * of code.
 *
 * This class extends the SimilarChunksWithPaths class, providing additional functionality for handling Java code.
 */
class JavaSimilarChunks : SimilarChunksWithPaths() {
    override fun calculate(text: String): SimilarChunkContext {
        TODO("Not yet implemented")
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