package cc.unitmesh.pick.related;

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JavaSimilarChunksTest {
    val similarChunks = JavaSimilarChunks()

    @Test
    fun should_return_empty_string_for_empty_input() {
        // Given
        val paths = emptyList<String>()

        // When
        val result = similarChunks.calculateCommonPath(paths)

        // Then
        assertEquals("", result)
    }

    @Test
    fun should_return_single_element_for_single_element_input() {
        // Given
        val paths = listOf("org.unitmesh.controller.BlogController")

        // When
        val result = similarChunks.calculateCommonPath(paths)

        // Then
        assertEquals("org.unitmesh.controller.BlogController", result)
    }

    @Test
    fun should_return_common_path_for_multiple_elements_with_common_path() {
        // Given
        val paths = listOf(
            "org.unitmesh.controller.BlogController",
            "org.unitmesh.service.BlogService",
            "org.unitmesh.model.BlogModel"
        )

        // When
        val result = similarChunks.calculateCommonPath(paths)

        // Then
        assertEquals("org.unitmesh", result)
    }

    @Test
    fun should_return_empty_string_for_multiple_elements_with_different_common_paths() {
        // Given
        val paths = listOf(
            "org.unitmesh.controller.BlogController",
            "org.example.service.BlogService",
            "cc.unitmesh.model.BlogModel"
        )

        // When
        val result = similarChunks.calculateCommonPath(paths)

        // Then
        assertEquals("", result)
    }
}
