package cc.unitmesh.quality.comment;

import chapi.ast.javaast.JavaAnalyser
import chapi.domain.core.CodeDataStruct
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommentAnalyserTest {

    @Test
    fun `should return empty list when analysis nodes given empty nodes`() {
        // given
        val nodes = ArrayList<CodeDataStruct>()

        // when
        val commentAnalyser = CommentAnalyser(mapOf())
        val result = commentAnalyser.analysis(nodes)

        // then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return list one issue when given lost parameter`() {
        // given
        val code = """
            public class Test {
                /**
                 * Sum a and b, and return the result.
                 * @param x the first number
                 * @return the result of x + y
                 */
                public int calculateSum(int x, int y) {
                    return x + y;
                }
            }
            """.trimIndent()
        val container = JavaAnalyser().analysis(code, "Test.java")
        container.Content = code
        val commentAnalyser = CommentAnalyser(mapOf())

        // when
        val result = commentAnalyser.analysis(container)

        // then
        assertEquals(1, result.size)
        assertEquals("MissingParameterDesc", result[0].name)
    }
}