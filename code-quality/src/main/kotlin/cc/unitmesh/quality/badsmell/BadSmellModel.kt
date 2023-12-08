package cc.unitmesh.quality.badsmell

import org.archguard.rule.core.Issue
import org.archguard.rule.core.IssuePosition
import org.archguard.rule.core.RuleType
import org.archguard.rule.core.Severity

data class BadSmellModel(
    val file: String? = null,
    val line: String? = null,
    val bs: SmellType? = null,
    val description: String? = null,
    val size: Int? = null,
) {
    fun toIssue(): Issue {
        return Issue(
            name = bs?.name ?: "",
            fullName = file ?: "",
            detail = description ?: "",
            position = IssuePosition(),
            ruleId = bs?.name ?: "",
            severity = Severity.WARN,
            source = line ?: "",
            ruleType = RuleType.CODE_SMELL,
        )
    }
}