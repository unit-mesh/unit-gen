package cc.unitmesh.core

enum class SupportedLang(val extension: String) {
    JAVA("java"),
    TYPESCRIPT("ts"),
    ;

    companion object {
        fun from(value: String): SupportedLang? {
            return when (value.lowercase()) {
                "java" -> JAVA
                "typescript" -> TYPESCRIPT
                else -> null
            }
        }

        fun all(): List<SupportedLang> {
            return listOf(JAVA, TYPESCRIPT)
        }
    }
}
