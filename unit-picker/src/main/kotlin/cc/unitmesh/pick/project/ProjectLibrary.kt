package cc.unitmesh.pick.project

import org.archguard.scanner.core.sca.CompositionDependency

object ProjectLibrary {
    /**
     *
     * ```kotlin
     *
     *
     */
    fun prepare(deps: List<CompositionDependency>): TestStack {
        val testStack = TestStack()
        var hasMatchSpringMvc = false
        var hasMatchSpringData = false

        deps.forEach { dep ->
            SpringLibrary.SPRING_MVC.forEach {
                it.coords.forEach { coord ->
                    if (dep.name.contains(coord)) {
                        testStack.coreFrameworks.putIfAbsent(it.shortText, true)
                        hasMatchSpringMvc = true
                    }
                }
            }

            SpringLibrary.SPRING_DATA.forEach {
                it.coords.forEach { coord ->
                    if (dep.name.contains(coord)) {
                        testStack.coreFrameworks.putIfAbsent(it.shortText, true)
                        hasMatchSpringData = true
                    }
                }
            }

            when {
                dep.name.contains("org.springframework.boot:spring-boot-test") -> {
                    testStack.testFrameworks.putIfAbsent("Spring Boot Test", true)
                }

                dep.name.contains("org.assertj:assertj-core") -> {
                    testStack.testFrameworks.putIfAbsent("AssertJ", true)
                }

                dep.name.contains("org.junit.jupiter:junit-jupiter") -> {
                    testStack.testFrameworks.putIfAbsent("JUnit 5", true)
                }

                dep.name.contains("org.mockito:mockito-core") -> {
                    testStack.testFrameworks.putIfAbsent("Mockito", true)
                }

                dep.name.contains("com.h2database:h2") -> {
                    testStack.testFrameworks.putIfAbsent("H2", true)
                }
            }
        }

        return testStack
    }
}