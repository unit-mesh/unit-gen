package cc.unitmesh.core

enum class SupportedLang {
    JAVA,
    ;

    companion object {
        fun from(value: String): SupportedLang? {
            return when (value.lowercase()) {
                "java" -> JAVA
                else -> null
            }
        }

        fun all(): List<SupportedLang> {
            return listOf(JAVA)
        }
    }
}
