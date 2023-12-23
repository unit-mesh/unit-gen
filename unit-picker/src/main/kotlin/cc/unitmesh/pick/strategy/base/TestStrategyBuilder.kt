package cc.unitmesh.pick.strategy.base

import cc.unitmesh.core.unittest.TypedTestIns
import cc.unitmesh.quality.CodeQualityType
import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct

interface TestStrategyBuilder {
    fun build(): List<TypedTestIns>

    fun hasIssue(node: CodeDataStruct, types: List<CodeQualityType>): Boolean {
        return QualityAnalyser.create(types).map { analyser ->
            analyser.analysis(listOf(node))
        }.flatten().isEmpty()
    }
}


