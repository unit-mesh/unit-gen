package cc.unitmesh.quality.badsmell

data class BadSmellModel(
    val file: String? = null,
    val line: String? = null,
    val bs: SmellType? = null,
    val description: String? = null,
    val size: Int? = null,
)