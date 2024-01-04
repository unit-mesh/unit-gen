package cc.unitmesh.pick.project.frameworks

import cc.unitmesh.core.SupportedLang
import org.archguard.scanner.core.sca.CompositionDependency

/**
 * The `TestFrameworkIdentifier` class is responsible for identifying the test frameworks used in a given project.
 * It takes a `SupportedLang` parameter to determine the programming language of the project and a list of `CompositionDependency` objects representing the project's dependencies.
 *
 * The `identify` method is used to identify the test frameworks based on the programming language. It returns a list of strings representing the identified test frameworks.
 *
 * The `TestFrameworkIdentifier` class supports the following programming languages:
 * - Java
 * - Kotlin
 * - TypeScript
 * - Rust (TODO: Not implemented yet)
 *
 * For the Kotlin language, the `identify` method calls the `identifyJava` method, as Kotlin test frameworks are compatible with Java.
 *
 * For the TypeScript language, the `identifyTypescript` method is called. It filters the dependencies list based on the predefined `testFrameworkList` and returns the identified test frameworks.
 *
 * The `TestFrameworkIdentifier` class also includes a companion object that defines the `testFrameworkList`. This list contains the names of popular test frameworks used in TypeScript projects.
 *
 * The `identifyJava` method is used to identify the test frameworks for Java projects. It iterates over the dependencies list and checks if each dependency is a known Java test framework. If a match is found, the corresponding test framework is added to the list of identified test frameworks.
 *
 * The `TestFrameworkIdentifier` class includes a `javaTestFrameworkMap` that maps the Maven coordinates of Java test frameworks to their corresponding names. This map is used in the `identifyJava` method to determine the names of the identified test frameworks.
 *
 * Note: The `TestFrameworkIdentifier` class does not currently support identifying test frameworks for Rust projects. This functionality is marked as TODO and will be implemented in the future.
 *
 * Example usage:
 * ```
 * val dependencies = listOf(
 *     CompositionDependency("junit", "junit", "4.12"),
 *     CompositionDependency("org.mockito", "mockito-core", "3.11.2"),
 *     CompositionDependency("jest", "jest", "27.0.6")
 * )
 *
 * val identifier = TestFrameworkIdentifier(SupportedLang.JAVA, dependencies)
 * val identifiedFrameworks = identifier.identify()
 * println(identifiedFrameworks) // Output: [JUnit, mockito]
 * ```
 */
class TestFrameworkIdentifier(val language: SupportedLang, private val dependencies: List<CompositionDependency>) {
    fun identify(): List<String> {
        return when (language) {
            SupportedLang.JAVA -> identifyJava()
            SupportedLang.KOTLIN -> identifyJava()
            SupportedLang.TYPESCRIPT -> identifyTypescript()
            SupportedLang.RUST -> TODO()
        }
    }

    private fun identifyTypescript(): List<String> {
        val testFrameworks = dependencies
            .filter { it.depName in testFrameworkList }
            .map { it.depName }

        return testFrameworks
    }

    // 在类中定义 testFrameworkList，使其可复用
    companion object {
        private val testFrameworkList = listOf(
            "jest",
            "mocha",
            "jasmine",
            "ava",
            "tape",
            "qunit",
            "web-component-tester",
            "testem",
            "webdriverio",
            "nightwatch",
            "puppeteer",
            "protractor",
            "cypress",
            "test"
        )
    }

    private val javaTestFrameworkMap: Map<String, List<String>> = mapOf(
        "junit:junit" to listOf("JUnit"),
        "org.testng:testng" to listOf("testng"),
        "org.spockframework:spock-core" to listOf("spock"),
        "io.cucumber:cucumber-java" to listOf("cucumber"),
        "com.intuit.karate:karate-junit5" to listOf("Karate JUnit5"),
        "org.jbehave:jbehave-core" to listOf("jbehave"),
        "org.jgiven:jgiven-junit5" to listOf("JGiven JUnit5"),
        "org.concordion:concordion" to listOf("concordion"),
        "org.junit.jupiter:junit-jupiter" to listOf("JUnit5"),
        "org.assertj:assertj-core" to listOf("assertj"),
        "org.hamcrest:hamcrest" to listOf("hamcrest"),
        "com.google.truth:truth" to listOf("truth"),
        "org.easytesting:fest-assert" to listOf("fest"),
        "org.easetech:easytest-core" to listOf("easytest"),
        "org.jmockit:jmockit" to listOf("jmockit"),
        "org.mockito:mockito-core" to listOf("mockito"),
        "org.powermock:powermock-core" to listOf("powermock"),
        "io.mockk:mockk" to listOf("mockk"),
        "com.github.tomakehurst:wiremock" to listOf("wiremock"),
        // based on https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-test/dependencies
        "org.springframework.boot:spring-boot-starter-test" to listOf(
            "Spring Boot Test", "Spring Test"
        )
    )
    private val javaFrameworkList = javaTestFrameworkMap.keys.toList()

    private fun identifyJava(): List<String> {
        val testFrameworks = mutableListOf<String>()

        for (dep in dependencies) {
            val element = dep.depGroup + ":" + dep.depArtifact
            javaFrameworkList.contains(element).let {
                if (it) {
                    testFrameworks.addAll(javaTestFrameworkMap[element]!!)
                }
            }
        }

        return testFrameworks
    }
}
