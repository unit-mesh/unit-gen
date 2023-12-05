package cc.unitmesh.eval.picker

data class PickerConfig(
    val url: String,
    val branch: String = "master",
    val baseDir: String = "."
) {
}