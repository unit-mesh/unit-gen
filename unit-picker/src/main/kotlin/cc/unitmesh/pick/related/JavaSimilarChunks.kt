package cc.unitmesh.pick.related

import cc.unitmesh.core.intelli.SimilarChunkContext
import cc.unitmesh.core.intelli.SimilarChunksWithPaths

/**
 * This class is used to calculate the similar chunks of Java code.
 * With different strategies (recent file strategy and chunk strategy), this class can find and analyze similar chunks
 * of code.
 *
 * Recent file strategy:
 *
 * - Strategy 1: Analysis by imports. This strategy analyzes the code by its imports and finds similar chunks.
 * - Strategy 2: Based on similarity in file name. This strategy identifies similar chunks based on similarities
 *   found in the file names.
 *
 * Chunk Strategy:
 * - Strategy 1: Analysis of the Abstract Syntax Tree (AST) of the code. This strategy splits the code into chunks
 *   based on the AST and then finds similar chunks.
 * - Strategy 2: Splitting the code by lines. This strategy splits the code into chunks based on line-breaks and
 *   then finds similar chunks.
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