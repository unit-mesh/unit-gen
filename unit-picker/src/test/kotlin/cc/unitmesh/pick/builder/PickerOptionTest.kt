package cc.unitmesh.pick.builder;

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PickerOptionTest {
    @Test
    fun should_return_correct_pure_data_file_name() {

        // When
        val result = PickerOption(
            url = "https://github.com/functionaljava/functionaljava",
            branch = "series/5.x",
        ).pureDataFileName()

        // Then
        assertEquals("datasets/https___github.com_functionaljava_functionaljava_series_5.x_java.json.jsonl", result)
    }
}