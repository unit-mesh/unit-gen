package cc.unitmesh.quality.comment.rule

import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import org.archguard.rule.core.IssueEmit
import org.archguard.rule.core.Rule
import org.archguard.rule.core.RuleContext

open class CommentRule : Rule() {
    fun visitRoot(node: CodeDataStruct, comment: String, context: RuleContext, callback: IssueEmit) {

    }

    fun visitFunction(node: CodeFunction, comment: String, context: RuleContext, callback: IssueEmit) {

    }
}