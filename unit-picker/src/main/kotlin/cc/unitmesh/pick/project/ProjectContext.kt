package cc.unitmesh.pick.project

import cc.unitmesh.core.SupportedLang
import kotlinx.serialization.Serializable
import org.archguard.scanner.core.sca.CompositionDependency

@Serializable
data class ProjectContext(
    var language: SupportedLang = SupportedLang.JAVA,
    var compositionDependency: List<CompositionDependency> = listOf(),
    val baseDir: String = ""
) {
    var testFrameworks: List<String>
    var coreFrameworks: List<String>

    private val testStack: TestStack by lazy {
        ProjectLibrary.prepare(language, compositionDependency)
    }

    init {
        coreFrameworks = testStack.coreFrameworks()
        testFrameworks = testStack.testFrameworks()
    }
}