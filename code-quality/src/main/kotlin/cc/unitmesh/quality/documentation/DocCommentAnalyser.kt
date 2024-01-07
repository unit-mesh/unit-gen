package cc.unitmesh.quality.documentation

import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct
import org.archguard.rule.core.Issue

class DocCommentAnalyser(comments: List<CodeComment>, thresholds: Map<String, Int>) : QualityAnalyser {
    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        return listOf()
    }
}
