package cc.unitmesh.quality.comment.rule

import chapi.domain.core.CodeFunction
import org.archguard.rule.core.IssueEmit
import org.archguard.rule.core.IssuePosition
import org.archguard.rule.core.RuleContext

/**
 * Parse the documentation of the code and check whether the documentation is complete.
 *
 * For example, the following code is missing the parameter description:
 * ```java
 * /**
 *  * Sum a and b, and return the result.
 *  * @param a the first number
 *  * @return the result of a + b
 * */
 * public int calculateSum(int a, int b) {
 *    return a + b;
 * }
 * ```
 *
 * We can use this rule to check whether the documentation is complete.
 */
class MissingParameterDescRule : CommentRule() {
    private val pattern = Regex("""@param\s+(\w+)\s+([^@]+)""")

    override fun visitFunction(node: CodeFunction, comment: String, context: RuleContext, callback: IssueEmit) {
        val matches = pattern.findAll(comment)

        val nodeSize = node.Parameters.size

        if (matches.count() != nodeSize) {
            callback(this, IssuePosition())
        }

        val matchNames = matches.map { it.groupValues[1] }.toSet()
        val nodeNames = node.Parameters.map { it.TypeType }.toSet()

        if (matchNames != nodeNames) {
            callback(this, IssuePosition())
        }
    }
}