package cc.unitmesh.pick.worker

import org.archguard.scanner.core.sca.CompositionDependency

class TestFrameworkIdentifier(val language: String, private val dependencies: List<CompositionDependency>) {
    fun identify(): List<String> {
        return when (language) {
            "java" -> identifyJava()
            "typescript" -> identifyTypescript()
            else -> listOf()
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
        "junit:junit" to listOf("junit"),
        "org.testng:testng" to listOf("testng"),
        "org.spockframework:spock-core" to listOf("spock"),
        "io.cucumber:cucumber-java" to listOf("cucumber"),
        "com.intuit.karate:karate-junit5" to listOf("karate-junit5"),
        "org.jbehave:jbehave-core" to listOf("jbehave"),
        "org.jgiven:jgiven-junit5" to listOf("jgiven-junit5"),
        "org.concordion:concordion" to listOf("concordion"),
        "org.junit.jupiter:junit-jupiter" to listOf("junit5"),
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
            "spring-test", "spring-boot-test", "junit", "assertj", "hamcrest", "mockito", "jsonassert", "json-path"
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
