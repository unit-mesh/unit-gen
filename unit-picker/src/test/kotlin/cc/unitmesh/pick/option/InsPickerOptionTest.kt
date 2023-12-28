package cc.unitmesh.pick.option

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InsPickerOptionTest {
    @Test
    fun should_return_correct_pure_data_file_name() {

        // When
        val result = InsPickerOption(
            url = "https://github.com/functionaljava/functionaljava",
            branch = "series/5.x",
            completionTypeSize  = 10,
            maxCharInCode = 100,
        ).pureDataFileName()

        // Then
        assertEquals("datasets/https___github.com_functionaljava_functionaljava_series_5.x_java.jsonl.jsonl", result)
    }

    @Test
    fun should_return_correct_repo_file_name() {
        // Given
        val insPickerOption = InsPickerOption(
            url = "https://github.com/example/repo.git",
            branch = "main",
            completionTypeSize  = 10,
            maxCharInCode = 100,
        )

        // When
        val result = insPickerOption.repoFileName()

        // Then
        assertEquals("https___github.com_example_repo.git_main_java.jsonl", result)
    }

    @Test
    fun should_encode_file_name_with_special_characters() {
        // Given
        val insPickerOption = InsPickerOption(
            url = "https://github.com/example/repo*:?<>.|",
            completionTypeSize  = 10,
            maxCharInCode = 100,
        )

        // When
        val result = insPickerOption.encodeFileName(insPickerOption.url)

        // Then
        assertEquals("https___github.com_example_repo_____._", result)
    }
}