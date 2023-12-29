package cc.unitmesh.pick.builder.comment

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class KotlinCommentBuilderTest {

    @Test
    fun `should extract KDoc comments when valid code provided`() {
        // Given
        val kotlinCode = """
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
    fun add(member: T): Int { ... }

    /**
     * Another function with KDoc.
     */
    fun anotherFunction() { ... }
}
"""

        // When
        val result = KotlinCommentBuilder.extractKdocComments(kotlinCode)

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
}
