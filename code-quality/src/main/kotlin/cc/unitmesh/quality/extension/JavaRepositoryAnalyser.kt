package cc.unitmesh.quality.extension

import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct
import org.archguard.linter.rule.sql.DatamapRuleVisitor
import org.archguard.linter.rule.sql.SqlRuleSetProvider
import org.archguard.rule.core.Issue
import org.archguard.scanner.analyser.database.JvmSqlAnalyser

class JavaRepositoryAnalyser(thresholds: Map<String, Int> = mapOf()) : QualityAnalyser {
    private val sqlRuleSetProvider = SqlRuleSetProvider()
    private val sqlAnalyser = JvmSqlAnalyser()


    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        val relations = nodes.flatMap { data ->
            sqlAnalyser.analysisByNode(data, "")
        }

        return DatamapRuleVisitor(relations).visitor(listOf(sqlRuleSetProvider.get()))
    }
}