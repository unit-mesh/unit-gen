package cc.unitmesh.quality.extension.rule

import chapi.domain.core.CodeDataStruct
import org.archguard.rule.core.IssueEmit
import org.archguard.rule.core.IssuePosition
import org.archguard.rule.core.Severity

/**
 * Service should not dependent more than 5 repositories.
 */
const val LIMIT = 5

class TooManyRepositoryDependenciesRule : ServiceRule() {
    init {
        this.id = "too-many-repository-dependencies"
        this.name = "TooManyRepositoryDependencies"
        this.key = this.javaClass.name
        this.severity = Severity.WARN
        this.description = "Service should not dependent more than 5 repositories."
    }

    override fun visitRoot(rootNodes: List<CodeDataStruct>, callback: IssueEmit) {
        rootNodes.forEach {
            val repositoryCount = it.Fields.filter { it.TypeType.contains("Repository", true) }.count()
            if (repositoryCount > LIMIT) {
                callback(this, IssuePosition())
            }
        }
    }
}