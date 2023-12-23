package cc.unitmesh.pick

enum class SupportedLang {
    JAVA,
    ;

    companion object {
        fun all(): List<SupportedLang> {
            return listOf(JAVA)
        }
    }
}
