package cc.unitmesh.quality.extension

import chapi.domain.core.CodeDataStruct
import org.archguard.linter.rule.sql.DatamapRuleVisitor
import org.archguard.linter.rule.sql.SqlRuleSetProvider
import org.archguard.linter.rule.webapi.WebApiRuleSetProvider
import org.archguard.linter.rule.webapi.WebApiRuleVisitor
import org.archguard.rule.core.Issue
import org.archguard.scanner.analyser.backend.JavaApiAnalyser
import org.archguard.scanner.analyser.database.JvmSqlAnalyser

class JavaExtRuleAnalyser {
    private val webApiRuleSetProvider = WebApiRuleSetProvider()
    private val sqlRuleSetProvider = SqlRuleSetProvider()
    private val sqlAnalyser = JvmSqlAnalyser()
    private val apiAnalyser = JavaApiAnalyser()

    fun checkApi(input: List<CodeDataStruct>): List<Issue> {
        input.forEach { data ->
            apiAnalyser.analysisByNode(data, "")
        }
        val services = apiAnalyser.toContainerServices()
        return WebApiRuleVisitor(services).visitor(listOf(webApiRuleSetProvider.get()))
    }

    fun checkSql(input: List<CodeDataStruct>): List<Issue> {
        val relations = input.flatMap { data ->
            sqlAnalyser.analysisByNode(data, "")
        }

        return DatamapRuleVisitor(relations).visitor(listOf(sqlRuleSetProvider.get()))
    }

    // Singleton
    companion object {
        private var instance: JavaExtRuleAnalyser? = null

        fun getInstance(): JavaExtRuleAnalyser {
            if (instance == null) {
                instance = JavaExtRuleAnalyser()
            }

            return instance!!
        }
    }
}