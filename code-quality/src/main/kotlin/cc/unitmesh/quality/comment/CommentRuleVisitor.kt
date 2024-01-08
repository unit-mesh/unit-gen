package cc.unitmesh.quality.comment

import cc.unitmesh.quality.comment.rule.CommentRule
import chapi.domain.core.CodeContainer
import org.archguard.rule.core.Issue
import org.archguard.rule.core.RuleContext
import org.archguard.rule.core.RuleSet
import org.archguard.rule.core.RuleVisitor

class CommentRuleVisitor(val comments: List<CodeComment>, container: CodeContainer) : RuleVisitor(comments) {
    override fun visitor(ruleSets: Iterable<RuleSet>): List<Issue> {
        val results: MutableList<Issue> = mutableListOf()
        val context = RuleContext()

        ruleSets.forEach { ruleSet ->
            ruleSet.rules.forEach { rule ->
                val apiRule = rule as CommentRule
//                resources.map {
//                    apiRule.visitResource(it, context, fun(rule: Rule, position: IssuePosition) {
//                        results += Issue(
//                            position,
//                            ruleId = rule.key,
//                            name = rule.name,
//                            detail = rule.description,
//                            ruleType = RuleType.HTTP_API_SMELL,
//                            fullName = "${it.packageName}:${it.className}:${it.methodName}",
//                            source = "${it.sourceHttpMethod} ${it.sourceUrl}"
//                        )
//                    })
//                }
            }
        }

        return results
    }
}
