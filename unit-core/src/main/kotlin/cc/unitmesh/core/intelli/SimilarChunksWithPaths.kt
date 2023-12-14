package cc.unitmesh.core.intelli

import org.jetbrains.annotations.TestOnly
import java.io.File

abstract class SimilarChunksWithPaths(var snippetLength: Int = 60, private var maxRelevantFiles: Int = 20) {
    abstract fun similarChunksWithPaths(text: String): SimilarChunkContext
    abstract fun extractChunks(mostRecentFiles: List<File>): List<List<String>>

    fun tokenLevelJaccardSimilarity(chunks: List<List<String>>, text: String): List<List<Double>> {
        val currentFileTokens: Set<String> = tokenize(text).toSet()
        return chunks.map { list ->
            list.map {
                val tokenizedFile: Set<String> = tokenize(it).toSet()
                similarityScore(currentFileTokens, tokenizedFile)
            }
        }
    }

    /**
     * since is slowly will tokenize, we revoke to same way will Copilot:
     * https://github.com/mengjian-github/copilot-analysis#promptelement%E4%B8%BB%E8%A6%81%E5%86%85%E5%AE%B9
     *
     */
    @TestOnly
    fun tokenize(chunk: String): List<String> {
        return chunk.split(Regex("[^a-zA-Z0-9]")).filter { it.isNotBlank() }
    }

    @TestOnly
    fun similarityScore(set1: Set<String>, set2: Set<String>): Double {
        val intersectionSize: Int = (set1 intersect set2).size
        val unionSize: Int = (set1 union set2).size
        return intersectionSize.toDouble() / unionSize.toDouble()
    }
}