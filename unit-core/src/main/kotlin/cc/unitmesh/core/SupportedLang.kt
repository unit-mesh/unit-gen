package cc.unitmesh.core

enum class SupportedLang(val extension: String) {
    JAVA("java"),
    TYPESCRIPT("ts"),
    KOTLIN("kt"),
    RUST("rs")
    ;

    companion object {
        fun from(value: String): SupportedLang? {
            return when (value.lowercase()) {
                "java" -> JAVA
                "typescript" -> TYPESCRIPT
                "kotlin" -> KOTLIN
                "rust" -> RUST
                else -> null
            }
        }

        fun all(): List<SupportedLang> {
            return listOf(JAVA, TYPESCRIPT, KOTLIN, RUST)
        }
    }
}
