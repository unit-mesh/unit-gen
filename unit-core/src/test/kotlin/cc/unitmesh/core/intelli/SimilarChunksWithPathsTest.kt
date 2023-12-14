package cc.unitmesh.core.intelli;

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class SimilarChunksWithPathsTest {

    class TestingChunks : SimilarChunksWithPaths() {
        override fun similarChunksWithPaths(text: String): SimilarChunkContext {
            TODO()
        }

        override fun extractChunks(mostRecentFiles: List<File>): List<List<String>> {
            return mostRecentFiles.map { listOf("chunk1", "chunk2") }
        }
    }

    private val similarChunksWithPaths: SimilarChunksWithPaths = TestingChunks()

    @Test
    fun should_return_jaccard_similarity_scores() {
        // Given
        val chunks = listOf(
            listOf("chunk1", "chunk2", "chunk4"),
            listOf("chunk3", "chunk4", "chunk5"),
            listOf("chunk4", "chunk6", "chunk7")
        )
        val text = "some chunk1 with chunk4"

        // When
        val result = similarChunksWithPaths.tokenLevelJaccardSimilarity(chunks, text)

        // Then
        val expected = listOf(
            listOf(0.25, 0.0, 0.25),
            listOf(0.0, 0.25, 0.0),
            listOf(0.25, 0.0, 0.0)
        )

        assertEquals(expected, result)
    }

    @Test
    fun should_tokenize_chunk_into_list_of_strings() {
        // Given
        val chunk = "some chunk with words"

        // When
        val result = similarChunksWithPaths.tokenize(chunk)

        // Then
        val expected = listOf("some", "chunk", "with", "words")
        assertEquals(expected, result)
    }

    @Test
    fun should_calculate_similarity_score() {
        // Given
        val set1 = setOf("word1", "word2", "word3")
        val set2 = setOf("word2", "word3", "word4")

        // When
        val result = similarChunksWithPaths.similarityScore(set1, set2)

        // Then
        val expected = 0.5
        assertEquals(expected, result)
    }
}