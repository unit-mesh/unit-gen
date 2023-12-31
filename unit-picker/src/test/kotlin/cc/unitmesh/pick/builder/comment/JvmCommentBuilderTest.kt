package cc.unitmesh.pick.builder.comment

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.comment.DocCommentToolType
import chapi.ast.kotlinast.KotlinAnalyser
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class JvmCommentBuilderTest {
    // Given
    private val kotlinCode = """
/**
 * A group of *members*.
 *
 * This class has no useful logic; it's just a documentation example.
 *
 * @param T the type of a member in this group.
 * @property name the name of this group.
 * @constructor Creates an empty group.
 */
class Group<T>(val name: String) {
    /**
     * Adds a [member] to this group.
     * @return the new size of the group.
     */
    fun add(member: T): Int {
      return 0 
    }

    /**
     * Another function with KDoc.
     */
    fun anotherFunction() { }
}
"""

    @Test
    fun `should extract KDoc comments when valid code provided`() {
        // When
        val result = extractComments(kotlinCode, SupportedLang.KOTLIN)

        // Then
        result.size shouldBe 3
        result[0].content shouldBe """
            /**
             * A group of *members*.
             *
             * This class has no useful logic; it's just a documentation example.
             *
             * @param T the type of a member in this group.
             * @property name the name of this group.
             * @constructor Creates an empty group.
             */
        """.trimIndent()
        result[1].content shouldBe """
            /**
             * Adds a [member] to this group.
             * @return the new size of the group.
             */
        """.trimIndent()
    }

    @Test
    fun `should extract KDoc comments when valid code provided 2`() {
        // Given
        val codeContainer = KotlinAnalyser().analysis(kotlinCode, "test.kt")

        // When
        val result = JvmCommentBuilder(SupportedLang.KOTLIN, DocCommentToolType.KOTLIN).build(kotlinCode, codeContainer)

        // Then
        result.size shouldBe 3
        result[0].toInstruction().output shouldBe """
            /**
             * A group of *members*.
             *
             * This class has no useful logic; it's just a documentation example.
             *
             * @param T the type of a member in this group.
             * @property name the name of this group.
             * @constructor Creates an empty group.
             */
        """.trimIndent()

        result[1].toInstruction().output shouldBe """
            /**
             * Adds a [member] to this group.
             * @return the new size of the group.
             */
        """.trimIndent()
    }
}
