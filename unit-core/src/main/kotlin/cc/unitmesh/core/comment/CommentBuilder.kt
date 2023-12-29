package cc.unitmesh.core.comment

import cc.unitmesh.core.completion.TypedIns
import chapi.domain.core.CodeContainer

/**
 * The CommentBuilder interface represents a builder for generating comments in code.
 *
 * The CommentBuilder interface provides properties and methods to configure and build comments.
 *
 * Properties:
 * - commentStart: A string representing the starting characters of a comment.
 * - commentEnd: A string representing the ending characters of a comment.
 * - docInstruction: An instance of the DocInstruction enum representing the type of documentation instruction.
 *
 * Methods:
 * - build(container: CodeContainer): Generates a list of TypedIns objects based on the provided CodeContainer.
 *
 * Example usage:
 *
 * Note: This interface does not provide any implementation details for the methods.
 *
 * @see CodeContainer
 * @see TypedIns
 * @see DocInstruction
 */
interface CommentBuilder {
    /// for match start with comment: `/**`
    val commentStart: String

    /// for match end with comment: `*/`
    val commentEnd: String

    /// for generate instruction
    val docInstruction: DocInstruction
    fun build(container: CodeContainer): List<TypedIns>
}
