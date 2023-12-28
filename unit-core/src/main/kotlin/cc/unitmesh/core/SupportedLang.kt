package cc.unitmesh.core

enum class SupportedLang(val extension: String) {
    JAVA("java"),
    TYPESCRIPT("ts"),
    KOTLIN("kt"),
    ;

    companion object {
        fun from(value: String): SupportedLang? {
            return when (value.lowercase()) {
                "java" -> JAVA
                "typescript" -> TYPESCRIPT
                "kotlin" -> KOTLIN
                else -> null
            }
        }

        fun all(): List<SupportedLang> {
            return listOf(JAVA, TYPESCRIPT, KOTLIN)
        }
    }
}
