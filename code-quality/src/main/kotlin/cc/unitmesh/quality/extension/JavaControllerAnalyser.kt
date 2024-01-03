package cc.unitmesh.quality.extension

import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct
import org.archguard.linter.rule.webapi.WebApiRule
import org.archguard.linter.rule.webapi.WebApiRuleSetProvider
import org.archguard.linter.rule.webapi.WebApiRuleVisitor
import org.archguard.rule.core.Issue
import org.archguard.scanner.analyser.backend.JavaApiAnalyser

/**
 * The `JavaControllerAnalyser` class is responsible for analyzing Java controller classes
 * to identify any quality issues based on a set of predefined rules.
 *
 * @param thresholds A map of thresholds for different quality metrics. The default value is an empty map.
 *
 * @constructor Creates a new instance of `JavaControllerAnalyser` with the specified thresholds.
 */
class JavaControllerAnalyser(thresholds: Map<String, Int> = mapOf()) : QualityAnalyser {
    private val webApiRuleSetProvider = WebApiRuleSetProvider()

    /**
     * This method analyzes a list of REST APIs which from code data structures and returns a list of issues.
     * It uses the Default ArchGuard [WebApiRule], which includes the following rules:
     *
     * - SpliceNamingRule
     * - NoCrudEndRule
     * - NotUppercaseRule
     * - StartWithoutCrudRule
     * - NoHttpMethodInUrlRule
     * - MinFeatureApiRule
     * - MultipleParametersRule
     *
     * @param nodes The list of code data structures to analyze
     * @return The list of issues found during the analysis
     */
    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        val apiAnalyser = JavaApiAnalyser()

        nodes.forEach { data ->
            apiAnalyser.analysisByNode(data, "")
        }
        val services = apiAnalyser.toContainerServices()
        return WebApiRuleVisitor(services).visitor(listOf(webApiRuleSetProvider.get()))
    }
}
