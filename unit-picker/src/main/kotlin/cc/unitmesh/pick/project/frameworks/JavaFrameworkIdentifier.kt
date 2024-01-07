package cc.unitmesh.pick.project.frameworks

import org.archguard.scanner.core.sca.CompositionDependency

class JavaFrameworkIdentifier(private val dependencies: List<CompositionDependency>) : LangFrameworkIdentifier {
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

    override fun testFramework(): List<String> {
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