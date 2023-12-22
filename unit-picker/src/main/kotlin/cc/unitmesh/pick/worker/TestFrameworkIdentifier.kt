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


    private fun identifyJava(): List<String> {
        val testFrameworks = mutableListOf<String>()
        // junit, testng, spock, cucumber, karate, jbehave, jgiven, concordion, junit5, test, assertj, hamcrest, truth, fest, easytest, jmockit, mockito, powermock, mockk, wiremock, rest-assured, restassured, res
        val frameworkDepName = listOf(
            "junit:junit",
            "org.testng:testng",
            "org.spockframework:spock-core",
            "io.cucumber:cucumber-java",
            "com.intuit.karate:karate-junit5",
            "org.jbehave:jbehave-core",
            "org.jgiven:jgiven-junit5",
            "org.concordion:concordion",
            "org.junit.jupiter:junit-jupiter",
            "org.assertj:assertj-core",
            "org.hamcrest:hamcrest",
            "com.google.truth:truth",
            "org.easytesting:fest-assert",
            "org.easetech:easytest-core",
            "org.jmockit:jmockit",
            "org.mockito:mockito-core",
            "org.powermock:powermock-core",
            "io.mockk:mockk",
            "com.github.tomakehurst:wiremock",
            "org.springframework.boot:spring-boot-starter-test",
        )

        for (dep in dependencies) {
            frameworkDepName.contains(dep.depGroup + ":" + dep.depArtifact).let {
                if (it) {
                    testFrameworks.add(dep.depName)
                }
            }
        }

        return testFrameworks
    }
}
