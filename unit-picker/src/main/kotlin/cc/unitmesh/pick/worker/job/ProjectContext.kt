package cc.unitmesh.pick.worker.job

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.worker.TestFrameworkIdentifier
import kotlinx.serialization.Serializable
import org.archguard.rule.common.Language
import org.archguard.scanner.core.sca.CompositionDependency

@Serializable
data class ProjectContext(
    var language: SupportedLang = SupportedLang.JAVA,
    var compositionDependency: List<CompositionDependency> = listOf(),
) {
    private var testFramework: List<String> = listOf()

    init {
        testFramework = TestFrameworkIdentifier(language, compositionDependency).identify()

    }
}