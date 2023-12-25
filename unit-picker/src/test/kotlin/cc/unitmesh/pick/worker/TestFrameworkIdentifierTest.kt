package cc.unitmesh.pick.worker;

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.ext.from
import cc.unitmesh.pick.project.frameworks.TestFrameworkIdentifier
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.archguard.scanner.core.sca.CompositionDependency
import org.junit.jupiter.api.Test

class TestFrameworkIdentifierTest {

    @Test
    fun shouldIdentifyJavaTestFrameworks() {
        // given
        val language = SupportedLang.JAVA
        val dependencies = listOf(
            CompositionDependency.from("junit:junit", "org.junit.jupiter", "junit-jupiter"),
            CompositionDependency.from("org.mockito:mockito-core", "org.mockito", "mockito-core"),
            CompositionDependency.from("com.intuit.karate:karate-junit5", "com.intuit.karate", "karate-junit5")
        )
        val testFrameworkIdentifier = TestFrameworkIdentifier(language, dependencies)

        // when
        val identifiedFrameworks = testFrameworkIdentifier.identify()

        identifiedFrameworks shouldBe listOf("JUnit5", "mockito", "Karate JUnit5")
    }

    @Test
    fun shouldIdentifyTypescriptTestFrameworks() {
        // given
        val language = SupportedLang.TYPESCRIPT
        val dependencies = listOf(
            CompositionDependency.from("jest", "some.group", "jest"),
            CompositionDependency.from("mocha", "another.group", "mocha"),
            CompositionDependency.from("jasmine", "third.group", "jasmine")
        )
        val testFrameworkIdentifier = TestFrameworkIdentifier(language, dependencies)

        // when
        val identifiedFrameworks = testFrameworkIdentifier.identify()

        // then
        val expectedFrameworks = listOf("jest", "mocha", "jasmine")
        assert(identifiedFrameworks == expectedFrameworks)
    }

    @Test
    fun shouldReturnMultipleForSpringTest() {
        // given
        val language = SupportedLang.JAVA
        val dependencies = listOf(
            CompositionDependency.from("org.springframework.boot:spring-boot-starter-test", "org.springframework.boot", "spring-boot-starter-test"),
        )
        val testFrameworkIdentifier = TestFrameworkIdentifier(language, dependencies)

        // when
        val identifiedFrameworks = testFrameworkIdentifier.identify()

        // then
        identifiedFrameworks shouldContain "Spring Test"
        identifiedFrameworks shouldContain "Spring Boot Test"
    }
}
