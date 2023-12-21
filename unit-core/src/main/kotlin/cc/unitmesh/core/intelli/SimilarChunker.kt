package cc.unitmesh.core.intelli

import java.io.File

/**
 * This class is used to find similar chunks with paths.
 * Should be implemented by each language
 */
abstract class SimilarChunker(
    var snippetLength: Int = 60,
    var maxRelevantFiles: Int = 20,
    val packageScoreThreshold: Double = 0.5,
    val codeScoreThreshold: Double = 0.1,
) {
    /**
     * Returns a list of the most recently edited files in the project.
     */
    abstract fun calculate(text: String, canonicalName: String): SimilarChunkContext

    /**
     * Calculates the token-level Jaccard similarity between a list of chunks and a given text.
     *
     * This method takes a list of chunks and a text as parameters and calculates the pairwise Jaccard similarity
     * between the tokens in each chunk and the tokens in the given text. The Jaccard similarity is a measure of
     * similarity between two sets, calculated as the size of their intersection divided by the size of their union.
     *
     * @param chunks the list of chunks to compare with the text
     * @param text the given text to compare with the chunks
     * @return a list of lists containing the similarity scores between each token in each chunk and the text
     *
     * @see tokenize for the tokenization process
     * @see similarityScore for the calculation of the Jaccard similarity score
     */
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
    fun tokenize(chunk: String): List<String> {
        return chunk.split(Regex("[^a-zA-Z0-9]")).filter { it.isNotBlank() }
    }

    /**
     * Calculates the similarity score between two sets of strings.
     *
     * The similarity score is calculated as the size of the intersection of the two sets divided by the size of the union of the two sets.
     *
     * @param set1 the first set of strings
     * @param set2 the second set of strings
     * @return the similarity score between the two sets, represented as a double value
     */
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
            .substringBeforeLast(".")
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
     * Removes some prefix path such as "src/main/kotlin", "src/main/java", "src/main/resources",
     * "src/test/kotlin", "src/test/java", and "src/test/resources" from the path chunks.
     * Then tokenizes the cleaned chunks and the given text.
     * Computes the Jaccard similarity score between the tokenized text and each tokenized chunk.
     *
     * @param chunks the list of path chunks to compare with the text
     * @param text the text to be compared with the path chunks
     * @return a list of Jaccard similarity scores, one for each path chunk
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

    fun packageNameTokenize(packageName: String): List<String> {
        return packageName
            .split(".")
            .flatMap { it.split(File.separatorChar) }
            .asSequence()
            .filter { it.isNotBlank() && !it.matches(Regex(".*\\d.*")) && !COMMON_WORDS.contains(it.lowercase()) }
            .flatMap { it.split("(?=[A-Z])".toRegex()) } // split by camel case
            .filter { it.isNotBlank() }
            .toList()
    }

    companion object {
        val COMMON_WORDS = setOf("src", "main", "kotlin", "java")
    }
}
