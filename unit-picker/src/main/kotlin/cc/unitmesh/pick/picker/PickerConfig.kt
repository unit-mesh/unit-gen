package cc.unitmesh.pick.picker

data class PickerConfig(
    val url: String,
    val branch: String = "master",
    val baseDir: String = "."
) {
}