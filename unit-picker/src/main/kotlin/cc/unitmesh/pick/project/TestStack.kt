package cc.unitmesh.pick.project

/**
 * TODO: change to dependency tree
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