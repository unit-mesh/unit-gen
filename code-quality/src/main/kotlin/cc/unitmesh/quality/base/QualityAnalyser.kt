package cc.unitmesh.quality.base

import chapi.domain.core.CodeDataStruct
import org.archguard.rule.core.Issue

interface QualityAnalyser {
    fun analysis(nodes: List<CodeDataStruct>): List<Issue>
}