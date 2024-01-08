package cc.unitmesh.quality.comment

import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct
import org.archguard.rule.core.Issue

class CommentAnalyser(comments: List<CodeComment>, thresholds: Map<String, Int>) : QualityAnalyser {
    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        return listOf()
    }
}
