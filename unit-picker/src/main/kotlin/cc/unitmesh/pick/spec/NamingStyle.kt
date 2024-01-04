package cc.unitmesh.pick.spec

import chapi.domain.core.CodeDataStruct

enum class NamingStyle(val value: String) {
    CAMEL_CASE("CamelCase"),
    SNAKE_CASE("snake_case"),
    KEBAB_CASE("kebab-case"),
    ;
}

fun CodeDataStruct.checkNamingStyle(): String {
    val countByNaming: MutableMap<NamingStyle, Int> = mutableMapOf(
        NamingStyle.CAMEL_CASE to 0,
        NamingStyle.SNAKE_CASE to 0,
        NamingStyle.KEBAB_CASE to 0,
    )

    this.Functions.map {
        val name = it.Name
        val nameStyle = when {
            name.contains("_") -> NamingStyle.SNAKE_CASE
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

fun checkCamelCase(name: String): Boolean {
    return name.matches(Regex("^[a-z][a-zA-Z0-9]*$"))
}
