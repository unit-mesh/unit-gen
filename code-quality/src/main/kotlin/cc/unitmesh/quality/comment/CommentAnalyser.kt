package cc.unitmesh.quality.comment

import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import org.archguard.rule.core.Issue

class CommentAnalyser(thresholds: Map<String, Int>) : QualityAnalyser {
    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> = listOf()

    override fun analysis(container: CodeContainer): List<Issue> {
        return listOf()
    }
}
