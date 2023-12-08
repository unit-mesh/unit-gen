package cc.unitmesh.quality.extension

import cc.unitmesh.quality.base.QualityAnalyser
import chapi.domain.core.CodeDataStruct
import org.archguard.linter.rule.webapi.WebApiRuleSetProvider
import org.archguard.rule.core.Issue

class JavaControllerAnalyser : QualityAnalyser {
    private val webApiRuleSetProvider = WebApiRuleSetProvider()

    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        // 检查 Service 长度，调用的 repository 数量，
        return listOf()
    }
}