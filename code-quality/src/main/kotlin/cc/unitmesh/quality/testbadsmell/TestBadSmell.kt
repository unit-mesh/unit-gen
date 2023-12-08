package cc.unitmesh.quality.testbadsmell

import kotlinx.serialization.Serializable
import org.archguard.rule.core.Issue
import org.archguard.rule.core.IssuePosition
import org.archguard.rule.core.RuleType
import org.archguard.rule.core.Severity

@Serializable
data class TestBadSmell(
    var fileName: String = "",
    var type: String = "",
    var description: String = "",
    var line: Int = 0,
) {
    fun toIssue(): Issue {
        return Issue(
            name = type,
            fullName = fileName,
            detail = description,
            position = IssuePosition(),
            ruleId = type,
            severity = Severity.WARN,
            source = line.toString(),
            ruleType = RuleType.TEST_CODE_SMELL,
        )
    }
}