package cc.unitmesh.quality.comment.rule

import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import org.archguard.rule.core.IssueEmit
import org.archguard.rule.core.Rule
import org.archguard.rule.core.RuleContext

open class CommentRule : Rule() {
    override fun visit(rootNode: Any, context: RuleContext, callback: IssueEmit) {

    }

    open fun visitDataStruct(dataStruct: CodeDataStruct, index: Int, callback: IssueEmit) {}
    open fun visitFunction(function: CodeFunction, index: Int, callback: IssueEmit) {}
}
