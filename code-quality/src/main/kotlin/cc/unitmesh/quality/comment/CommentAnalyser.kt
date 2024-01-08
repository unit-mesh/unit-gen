package cc.unitmesh.quality.comment

import cc.unitmesh.quality.QualityAnalyser
import cc.unitmesh.quality.comment.rule.CommentRuleSetProvider
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import org.archguard.rule.core.Issue

class CommentAnalyser(thresholds: Map<String, Int>) : QualityAnalyser {
    private val ruleSetProvider = CommentRuleSetProvider()
    private var comments: List<CodeComment> = mutableListOf()

    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        return listOf()
    }

    override fun analysis(container: CodeContainer): List<Issue> {
        comments = CodeComment.parseComment(container.Content)
        return CommentRuleVisitor(comments, container).visitor(listOf(ruleSetProvider.get()))
    }
}
