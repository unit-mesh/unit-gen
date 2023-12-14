package cc.unitmesh.core.intelli

import java.io.File

class SimilarChunksWithPaths(private var snippetLength: Int = 60, private var maxRelevantFiles: Int = 20) {
    private fun similarChunksWithPaths(text: String): SimilarChunkContext {
        TODO()
    }

    private fun extractChunks(mostRecentFiles: List<File>): List<List<String>> {
//        return mostRecentFiles.mapNotNull { file ->
//            psiFile?.text
//                ?.split("\n", limit = snippetLength)
//                ?.filter {
//                    !it.trim().startsWith("import ") && !it.trim().startsWith("package ")
//                }
//                ?.chunked(snippetLength)?.flatten()
//        }
        return listOf()
    }

    private fun tokenLevelJaccardSimilarity(chunks: List<List<String>>, text: String): List<List<Double>> {
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
    private fun tokenize(chunk: String): List<String> {
        return chunk.split(Regex("[^a-zA-Z0-9]")).filter { it.isNotBlank() }
    }

    /**
     * ```kotlin
     * fun calculateJaccardSimilarity(setA: Set<Any>, setB: Set<Any>): Double {
     *     val intersectionSize = setA.intersect(setB).size.toDouble()
     *     val unionSize = (setA union setB).size.toDouble()
     *     return intersectionSize / unionSize
     * }
     * ```
     */
    private fun similarityScore(set1: Set<String>, set2: Set<String>): Double {
        val intersectionSize: Int = (set1 intersect set2).size
        val unionSize: Int = (set1 union set2).size
        return intersectionSize.toDouble() / unionSize.toDouble()
    }
}