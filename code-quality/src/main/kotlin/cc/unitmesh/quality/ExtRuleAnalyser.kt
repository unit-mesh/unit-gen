package cc.unitmesh.quality

import org.archguard.linter.rule.sql.DatamapRuleVisitor
import org.archguard.linter.rule.sql.SqlRuleSetProvider
import org.archguard.linter.rule.webapi.WebApiRuleSetProvider
import org.archguard.linter.rule.webapi.WebApiRuleVisitor
import org.archguard.rule.core.Issue
import org.archguard.scanner.core.sourcecode.CodeDatabaseRelation
import org.archguard.scanner.core.sourcecode.ContainerService

class ExtRuleAnalyser {
    val webApiRuleSetProvider = WebApiRuleSetProvider()
    val sqlRuleSetProvider = SqlRuleSetProvider()

    fun checkApi(services: List<ContainerService>): List<Issue> {
        return WebApiRuleVisitor(services).visitor(listOf(webApiRuleSetProvider.get()))
    }

    fun checkSql(relations: List<CodeDatabaseRelation>): List<Issue> {
        return DatamapRuleVisitor(relations).visitor(listOf(sqlRuleSetProvider.get()))
    }
}