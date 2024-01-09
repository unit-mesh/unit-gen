package cc.unitmesh.quality.extension

import cc.unitmesh.quality.QualityAnalyser
import chapi.domain.core.CodeDataStruct
import org.archguard.linter.rule.webapi.WebApiRuleSetProvider
import org.archguard.rule.core.Issue

class JavaServiceAnalyser(thresholds: Map<String, Int> = mapOf()) : QualityAnalyser {
    private val webApiRuleSetProvider = WebApiRuleSetProvider()

    override fun analysis(nodes: List<CodeDataStruct>): List<Issue> {
        //todo rule2: 调用的repository数量 -- 数量越少越好
        //todo rule3: 调用的repository之间的关联度 -- ER关系上，关联度越强越好
        //todo rule4: 不依赖controller层相关概念，如：request/response -- 层与层之间依赖关系清晰
        //todo rule5: public filed数量 -- service应该都是private filed
        //pending rule6: 不被外界访问的public method -- 应归属到bad smell
        //pending rule1: service 长度小于x00行  -- bad smell large class

        // 检查 Service 长度，调用的 repository 数量，
        return listOf()
    }
}