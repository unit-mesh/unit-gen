package cc.unitmesh.pick.project

/**
 * This class represents a stack of technologies and dependencies for testing purposes.
 * It contains information about various core frameworks, test frameworks, dependencies,
 * and development dependencies.
 *
 * The stack is defined using four mutable maps: coreFrameworks, testFrameworks, deps,
 * and devDeps.
 *
 * - The coreFrameworks map stores the core frameworks used in the stack. Each framework
 *   is identified by its name (a String) and associated with a boolean value indicating
 *   whether it is enabled or not.
 *
 * - The testFrameworks map stores the test frameworks used in the stack. Similar to the
 *   coreFrameworks map, each framework is identified by its name and associated with a
 *   boolean value indicating its enabled status.
 *
 * - The deps map represents the external dependencies in the stack. Each dependency is
 *   identified by its name (a String) and associated with the version of that dependency
 *   (another String).
 *
 * - The devDeps map represents the development dependencies in the stack. Similar to the
 *   deps map, each dependency is identified by its name and associated with the version.
 *
 * Additionally, the TestStack class provides two utility methods:
 *
 * - The coreFrameworks() method returns a list of all the core frameworks used in the stack.
 *   It extracts the keys (framework names) from the coreFrameworks map and converts them to
 *   a list.
 *
 * - The testFrameworks() method returns a list of all the test frameworks used in the stack.
 *   It works similarly to the coreFrameworks() method, extracting the keys (test framework
 *   names) from the testFrameworks map and returning them as a list.
 *
 */
data class TestStack(
    val coreFrameworks: MutableMap<String, Boolean> = mutableMapOf(),
    val testFrameworks: MutableMap<String, Boolean> = mutableMapOf(),
    val deps: MutableMap<String, String> = mutableMapOf(),
    val devDeps: MutableMap<String, String> = mutableMapOf()
) {
    fun coreFrameworks(): List<String> {
        return coreFrameworks.keys.toList()
    }

    fun testFrameworks(): List<String> {
        return testFrameworks.keys.toList()
    }
}