package cc.unitmesh.quality.comment.rule;

import cc.unitmesh.quality.comment.CodeComment
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodeProperty
import org.archguard.rule.core.IssueEmit
import org.archguard.rule.core.IssuePosition
import org.archguard.rule.core.Rule
import org.archguard.rule.core.RuleContext
import org.junit.jupiter.api.Test;

import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MissingParameterDescRuleTest {

    @Test
    fun shouldNotEmitIssueWhenDocumentationIsComplete() {
        // Given
        val rule = MissingParameterDescRule()
        val function = CodeFunction()
        function.Parameters = listOf(CodeProperty(TypeType = "int", TypeValue = "a"), CodeProperty(TypeType = "int", TypeValue = "b"))
        val comment = """
            /**
             * Sum a and b, and return the result.
             * @param a the first number
             * @param b the second number
             * @return the result of a + b
             */
        """.trimIndent()
        val context = RuleContext()
        val callback = IssueEmitCallback()

        // When
        val parseComment = CodeComment.parseComment(comment)[0]
        rule.visitFunction(function, parseComment, context, callback)

        // Then
        assertFalse(callback.hasIssue())
    }

    @Test
    fun shouldEmitIssueWhenParameterDescriptionIsMissing() {
        // Given
        val rule = MissingParameterDescRule()
        val function = CodeFunction()
        function.Parameters = listOf(CodeProperty(TypeType = "int", TypeValue = "a"), CodeProperty(TypeType = "int", TypeValue = "b"))
        val comment = """
            /**
             * Sum a and b, and return the result.
             * @param a the first number
             * @return the result of a + b
             */
        """.trimIndent()
        val context = RuleContext()
        val callback = IssueEmitCallback()

        // When
        val parseComment = CodeComment.parseComment(comment)[0]
        rule.visitFunction(function, parseComment, context, callback)

        // Then
        assertTrue(callback.hasIssue())
    }

    @Test
    fun shouldEmitIssueWhenParameterNamesDoNotMatch() {
        // Given
        val rule = MissingParameterDescRule()
        val function = CodeFunction()
        function.Parameters = listOf(CodeProperty(TypeType = "int", TypeValue = "a"), CodeProperty(TypeType = "int", TypeValue = "b"))
        val comment = """
            /**
             * Sum a and b, and return the result.
             * @param x the first number
             * @param y the second number
             * @return the result of a + b
             */
        """.trimIndent()
        val context = RuleContext()
        val callback = IssueEmitCallback()

        // When
        val parseComment = CodeComment.parseComment(comment)[0]
        rule.visitFunction(function, parseComment, context, callback)

        // Then
        assertTrue(callback.hasIssue())
    }
}

class IssueEmitCallback : IssueEmit {
    private var hasIssue = false

    fun hasIssue(): Boolean {
        return hasIssue
    }

    override fun invoke(rule: Rule, position: IssuePosition) {
        hasIssue = true
    }
}
