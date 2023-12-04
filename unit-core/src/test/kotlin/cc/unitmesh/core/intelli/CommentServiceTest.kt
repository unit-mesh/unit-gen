package cc.unitmesh.core.intelli;

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CommentServiceTest {
    @Test
    fun givenLang_whenLineComment_thenReturnLineComment() {
        assertEquals("//", CommentService.getInstance().lineComment("Java"))
        assertEquals("#", CommentService.getInstance().lineComment("Python"))
        assertEquals("--", CommentService.getInstance().lineComment("SQL"))
        assertEquals("--", CommentService.getInstance().lineComment("PL/SQL"))
    }

    @Test
    fun givenUnknownLang_whenLineComment_thenReturnDefaultLineComment() {
        assertEquals("//", CommentService.getInstance().lineComment("Unknown"))
    }
}
