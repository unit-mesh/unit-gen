package cc.unitmesh.pick.worker;

import io.kotest.matchers.shouldBe
import org.archguard.scanner.core.sca.CompositionDependency
import org.junit.jupiter.api.Test

class TestFrameworkIdentifierTest {

    @Test
    fun shouldIdentifyJavaTestFrameworks() {
        // given
        val language = "java"
        val dependencies = listOf(
            CompositionDependency.default("junit:junit", "org.junit.jupiter", "junit-jupiter"),
            CompositionDependency.default("org.mockito:mockito-core", "org.mockito", "mockito-core"),
            CompositionDependency.default("com.intuit.karate:karate-junit5", "com.intuit.karate", "karate-junit5")
        )
        val testFrameworkIdentifier = TestFrameworkIdentifier(language, dependencies)

        // when
        val identifiedFrameworks = testFrameworkIdentifier.identify()

        identifiedFrameworks shouldBe listOf("junit5", "mockito", "karate-junit5")
    }

    @Test
    fun shouldIdentifyTypescriptTestFrameworks() {
        // given
        val language = "typescript"
        val dependencies = listOf(
            CompositionDependency.default("jest", "some.group", "jest"),
            CompositionDependency.default("mocha", "another.group", "mocha"),
            CompositionDependency.default("jasmine", "third.group", "jasmine")
        )
        val testFrameworkIdentifier = TestFrameworkIdentifier(language, dependencies)

        // when
        val identifiedFrameworks = testFrameworkIdentifier.identify()

        // then
        val expectedFrameworks = listOf("jest", "mocha", "jasmine")
        assert(identifiedFrameworks == expectedFrameworks)
    }

    @Test
    fun shouldReturnEmptyListForUnknownLanguage() {
        // given
        val language = "unknown"
        val dependencies = listOf(
            CompositionDependency.default("dummy", "some.group", "dummy"),
            CompositionDependency.default("test", "another.group", "test")
        )
        val testFrameworkIdentifier = TestFrameworkIdentifier(language, dependencies)

        // when
        val identifiedFrameworks = testFrameworkIdentifier.identify()

        // then
        assert(identifiedFrameworks.isEmpty())
    }
}

private fun CompositionDependency.Companion.default(
    name: String,
    group: String,
    artifactId: String,
): CompositionDependency {
    return CompositionDependency(
        name = name,
        depName = name,
        depGroup = group,
        depArtifact = artifactId,
        packageManager = "maven",
        depVersion = "1.0.0",
        version = "1.0.0",
        path = "some/path",
        depScope = "compile",
        id = "some-id",
        parentId = "some-parent-id",
    )
}
