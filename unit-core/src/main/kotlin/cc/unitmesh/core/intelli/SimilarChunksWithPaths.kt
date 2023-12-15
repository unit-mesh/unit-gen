package cc.unitmesh.core.intelli

import org.jetbrains.annotations.TestOnly
import java.io.File

/**
 * This class is used to find similar chunks with paths.
 * Should be implemented by each language
 */
abstract class SimilarChunksWithPaths(var snippetLength: Int = 60, private var maxRelevantFiles: Int = 20) {
    /**
     * Returns a list of the most recently edited files in the project.
     */
    abstract fun calculate(text: String): SimilarChunkContext

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
     * since is slowly will tokenize, we revoke the same way will Copilot:
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

    /**
     * Tokenizes a given path string into a list of separate words.
     *
     * The path string represents a file path and is tokenized as follows:
     * 1. The file extension is removed.
     * 2. The path is split by forward slash (/) or hyphen (-) characters.
     * 3. Empty strings are removed from the resulting list.
     * 4. Numeric values are removed from the list.
     * 5. Common words such as "src", "main", "kotlin", and "java" are removed.
     * 6. Camel case splits words if present.
     *
     * @param path The path string to be tokenized.
     * @return A list of individual words extracted from the given path string.
     */
    fun pathTokenize(path: String): List<String> {
        return path
            .substringBeforeLast(".") // remove file extension
            .split(Regex("[/\\-]"))
            .flatMap { it.split(File.separatorChar) }
            .asSequence()
            .filter { it.isNotBlank() && !it.matches(Regex(".*\\d.*")) && !COMMON_WORDS.contains(it.lowercase()) }
            .flatMap { it.split("(?=[A-Z])".toRegex()) } // split by camel case
            .filter { it.isNotBlank() }
            .toList()
    }

    /**
     * Calculates the path-level Jaccard similarity between a list of path chunks and a given text.
     * 1. remove some prefix path like "src/main/kotlin" and "src/main/java"
     * 2. run tokenizing
     */
    fun pathLevelJaccardSimilarity(chunks: List<String>, text: String): List<Double> {
        val cleanedChunks = chunks.map {
            it.replace("src/main/kotlin", "")
                .replace("src/main/java", "")
                .replace("src/main/resources", "")
                .replace("src/test/kotlin", "")
                .replace("src/test/java", "")
                .replace("src/test/resources", "")
        }

        val textTokens = pathTokenize(text)
        return cleanedChunks.map {
            val chunkTokens = pathTokenize(it)
            similarityScore(textTokens.toSet(), chunkTokens.toSet())
        }
    }

    companion object {
        val COMMON_WORDS = setOf("src", "main", "kotlin", "java")
    }
}
