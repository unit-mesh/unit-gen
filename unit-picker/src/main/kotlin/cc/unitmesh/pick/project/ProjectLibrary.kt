package cc.unitmesh.pick.project

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.worker.TestFrameworkIdentifier
import org.archguard.scanner.core.sca.CompositionDependency

object ProjectLibrary {
    /**
     * Prepare test stack from dependencies.
     *
     * This method takes a list of dependencies and prepares a test stack based on those dependencies.
     *
     * @param language The language of the project.
     * @param deps The list of dependencies to be used to prepare the test stack.
     * @return The test stack containing the core and test frameworks.
     *
     * Example usage:
     * ```kotlin
     * val dependencies = listOf(
     *     CompositionDependency.from("org.springframework.boot:spring-boot-starter-test", "org.springframework.boot", "spring-boot-starter-test")
     * )
     * val testStack = ProjectLibrary.prepare(dependencies)
     * println(testStack)
     * ```
     *
     * @see TestStack
     * @see CompositionDependency
     */
    fun prepare(language: SupportedLang = SupportedLang.JAVA, deps: List<CompositionDependency>): TestStack {
        val testStack = TestStack()

        deps.forEach { dep ->
            val name = dep.depName
            SpringLibrary.SPRING_MVC.forEach {
                if (name.contains(it.coords)) {
                    testStack.coreFrameworks.putIfAbsent(it.shortText, true)
                }
            }

            SpringLibrary.SPRING_DATA.forEach {
                it.coords.forEach { coord ->
                    if (name.contains(coord)) {
                        testStack.coreFrameworks.putIfAbsent(it.shortText, true)
                    }
                }
            }

            TestFrameworkIdentifier(language, deps).identify().forEach {
                testStack.testFrameworks.putIfAbsent(it, true)
            }
        }

        return testStack
    }
}