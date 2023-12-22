package cc.unitmesh.pick.worker

import org.archguard.scanner.core.sca.CompositionDependency

class TestFrameworkIdentifier(val language: String, val dependencies: List<CompositionDependency>) {
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

    private val javaTestFrameworkMap: Map<String, String> = mapOf(
        "junit:junit" to "junit",
        "org.testng:testng" to "testng",
        "org.spockframework:spock-core" to "spock",
        "io.cucumber:cucumber-java" to "cucumber",
        "com.intuit.karate:karate-junit5" to "karate-junit5",
        "org.jbehave:jbehave-core" to "jbehave",
        "org.jgiven:jgiven-junit5" to "jgiven-junit5",
        "org.concordion:concordion" to "concordion",
        "org.junit.jupiter:junit-jupiter" to "junit5",
        "org.assertj:assertj-core" to "assertj",
        "org.hamcrest:hamcrest" to "hamcrest",
        "com.google.truth:truth" to "truth",
        "org.easytesting:fest-assert" to "fest",
        "org.easetech:easytest-core" to "easytest",
        "org.jmockit:jmockit" to "jmockit",
        "org.mockito:mockito-core" to "mockito",
        "org.powermock:powermock-core" to "powermock",
        "io.mockk:mockk" to "mockk",
        "com.github.tomakehurst:wiremock" to "wiremock",
        "org.springframework.boot:spring-boot-starter-test" to "spring-boot-starter-test"
    )
    private val javaFrameworkList = javaTestFrameworkMap.keys.toList()

    private fun identifyJava(): List<String> {
        val testFrameworks = mutableListOf<String>()

        for (dep in dependencies) {
            val element = dep.depGroup + ":" + dep.depArtifact
            javaFrameworkList.contains(element).let {
                if (it) {
                    testFrameworks.add(javaTestFrameworkMap[element]!!)
                }
            }
        }

        return testFrameworks
    }
}
