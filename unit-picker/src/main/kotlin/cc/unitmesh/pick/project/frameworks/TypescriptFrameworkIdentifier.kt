package cc.unitmesh.pick.project.frameworks

import org.archguard.scanner.core.sca.CompositionDependency

class TypescriptFrameworkIdentifier(private val dependencies: List<CompositionDependency>) : LangFrameworkIdentifier {
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

    override fun testFramework(): List<String> {
        val testFrameworks = dependencies
            .filter { it.depName in testFrameworkList }
            .map { it.depName }

        return testFrameworks
    }
}