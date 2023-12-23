package cc.unitmesh.pick.project;

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.ext.from
import org.archguard.scanner.core.sca.CompositionDependency
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProjectLibraryTest {

    @Test
    fun should_prepareTestStack_when_dependenciesGiven() {
        // given
        val language = SupportedLang.JAVA
        val deps = listOf(
            CompositionDependency.from(
                "org.springframework.boot:spring-boot-starter-test",
                "org.springframework.boot",
                "spring-boot-starter-test"
            )
        )

        // when
        val testStack = ProjectLibrary.prepare(language, deps)

        // then
        assertEquals("Spring MVC, Spring WebFlux", testStack.coreFrameworks())
        assertEquals("Spring Boot Test, Spring Test", testStack.testFrameworks())
    }
}