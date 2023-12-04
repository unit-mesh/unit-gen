package cc.unitmesh.core.intelli;

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LangServiceTest {

    @Test
    fun givenLang_whenLineComment_thenReturnLineComment() {
        // given
        val lang = "Java"
        val expectedLineComment = "//"

        // when
        val result = LangService.getInstance().lineComment(lang)

        // then
        assertEquals(expectedLineComment, result)
    }

    @Test
    fun givenUnknownLang_whenLineComment_thenReturnDefaultLineComment() {
        // given
        val lang = "Unknown"
        val expectedLineComment = "//"

        // when
        val result = LangService.getInstance().lineComment(lang)

        // then
        assertEquals(expectedLineComment, result)
    }
}
