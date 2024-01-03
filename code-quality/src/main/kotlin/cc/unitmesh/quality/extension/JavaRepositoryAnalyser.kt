package cc.unitmesh.quality.extension

import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct
import org.archguard.linter.rule.sql.DatamapRuleVisitor
import org.archguard.linter.rule.sql.SqlRule
import org.archguard.linter.rule.sql.SqlRuleSetProvider
import org.archguard.linter.rule.webapi.WebApiRule
import org.archguard.rule.core.Issue
import org.archguard.scanner.analyser.database.JvmSqlAnalyser

class JavaRepositoryAnalyser(thresholds: Map<String, Int> = mapOf()) : QualityAnalyser {
    private val sqlRuleSetProvider = SqlRuleSetProvider()
    private val sqlAnalyser = JvmSqlAnalyser()

    /**
     * This method analyzes a list of SQL statements which from code data structures and returns a list of issues.
     * It uses the Default ArchGuard [SqlRule], which includes the following rules:
     *
     * - [org.archguard.linter.rule.webapi.rules.UnknownColumnSizeRule]
     * - [org.archguard.linter.rule.webapi.rules.LikeStartWithoutPercentRule]
     * - [org.archguard.linter.rule.webapi.rules.LimitTableNameLengthRule]
     * - [org.archguard.linter.rule.webapi.rules.SnakeCaseNamingRule]
     * - [org.archguard.linter.rule.webapi.rules.InsertWithoutField]
     * - [org.archguard.linter.rule.webapi.rules.LimitJoinsRule]
     * - [org.archguard.linter.rule.webapi.rules.AtLeastOnePrimaryKeyRule]
     * - [org.archguard.linter.rule.webapi.rules.LimitColumnSizeRule]
     *
     * @param nodes The list of code data structures to analyze
     * @return The list of issues found during the analysis
     */
    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        val relations = nodes.flatMap { data ->
            sqlAnalyser.analysisByNode(data, "")
        }

        return DatamapRuleVisitor(relations).visitor(listOf(sqlRuleSetProvider.get()))
    }
}