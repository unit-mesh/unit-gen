package cc.unitmesh.pick.project.spec

import chapi.domain.core.CodeDataStruct

enum class NamingStyle(val value: String) {
    CAMEL_CASE("CamelCase"),
    SNAKE_CASE("snake_case"),
    KEBAB_CASE("kebab-case"),
    ;
}

/**
 * This method checks the naming style used in the CodeDataStruct object.
 *
 * It counts the occurrences of different naming styles (camel case, snake case, kebab case) in the names of the functions
 * within the CodeDataStruct object. The naming style with the highest count is considered the predominant naming style.
 *
 * @return The predominant naming style as a string. Possible values are [NamingStyle.SNAKE_CASE], [NamingStyle.KEBAB_CASE], and [NamingStyle.CAMEL_CASE].
 */
fun CodeDataStruct.checkNamingStyle(): String {
    val countByNaming: MutableMap<NamingStyle, Int> = mutableMapOf(
        NamingStyle.CAMEL_CASE to 0,
        NamingStyle.SNAKE_CASE to 0,
        NamingStyle.KEBAB_CASE to 0,
    )

    this.Functions.map {
        val name = it.Name
        val nameStyle = when {
            checkSnakeCase(name) -> NamingStyle.SNAKE_CASE
            name.contains("-") -> NamingStyle.KEBAB_CASE
            checkCamelCase(name) -> NamingStyle.CAMEL_CASE
            else -> NamingStyle.CAMEL_CASE
        }

        countByNaming[nameStyle] = countByNaming[nameStyle]!! + 1
    }

    val maxCount = countByNaming.values.maxOrNull()!!
    val maxNamingStyle = countByNaming.filter { it.value == maxCount }.keys.first()

    return maxNamingStyle.value
}

/**
 * Checks if a given string follows the snake_case naming style.
 *
 * @param name The string to be checked.
 * @return `true` if the string follows the snake_case naming style, `false` otherwise.
 */
fun checkSnakeCase(name: String): Boolean {
    if(!name.contains("_")) return false

    // check _ is not the first or last character
    if(name.startsWith("_") || name.endsWith("_")) return false

    // check _ is not consecutive
    if(name.contains("__")) return false

    // check _ is not followed by a number
    if(name.contains(Regex("_\\d"))) return false

    // check _ is not followed by a capital letter
    if(name.contains(Regex("_[A-Z]"))) return false

    return true
}

/**
 * Checks if a given name follows the camel case naming style.
 *
 * @param name The name to be checked.
 * @return `true` if the name follows the camel case naming style, `false` otherwise.
 */
fun checkCamelCase(name: String): Boolean {
    return name.matches(Regex("^[a-z][a-zA-Z0-9]*$"))
}
