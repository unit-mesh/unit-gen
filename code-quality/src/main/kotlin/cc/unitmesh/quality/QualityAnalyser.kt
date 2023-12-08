package cc.unitmesh.quality

import cc.unitmesh.quality.badsmell.BadsmellAnalyser
import cc.unitmesh.quality.extension.JavaControllerAnalyser
import cc.unitmesh.quality.extension.JavaRepositoryAnalyser
import cc.unitmesh.quality.extension.JavaServiceAnalyser
import cc.unitmesh.quality.testbadsmell.TestBadsmellAnalyser

import chapi.domain.core.CodeDataStruct
import org.archguard.rule.core.Issue

interface QualityAnalyser {
    fun analysis(nodes: List<CodeDataStruct>): List<Issue>

    companion object {
        /**
         * Creates a list of QualityAnalyser objects based on the given list of CodeQualityType.
         *
         * @param types The list of CodeQualityType to create QualityAnalyser objects for.
         * @param thresholds The map of thresholds for each CodeQualityType. Defaults to an empty map if not provided.
         * @return A list of QualityAnalyser objects corresponding to the given CodeQualityType.
         */
        fun create(types: List<CodeQualityType>, thresholds: Map<String, Int> = mapOf()): List<QualityAnalyser> {
            return types.map { type ->
                when (type) {
                    CodeQualityType.BadSmell -> BadsmellAnalyser(thresholds)
                    CodeQualityType.TestBadSmell -> TestBadsmellAnalyser(thresholds)
                    CodeQualityType.JavaController -> JavaControllerAnalyser(thresholds)
                    CodeQualityType.JavaRepository -> JavaRepositoryAnalyser(thresholds)
                    CodeQualityType.JavaService -> JavaServiceAnalyser(thresholds)
                }
            }
        }
    }
}