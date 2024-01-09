package cc.unitmesh.quality.extension.rule

import chapi.domain.core.CodeDataStruct
import org.archguard.rule.core.IssueEmit
import org.archguard.rule.core.Rule

open class ServiceRule : Rule() {
    open fun visitRoot(rootNode: List<CodeDataStruct>, callback: IssueEmit) {}
}