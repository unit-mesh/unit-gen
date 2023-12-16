package cc.unitmesh.pick.builder

import cc.unitmesh.pick.prompt.CodeContextStrategy
import cc.unitmesh.quality.CodeQualityType
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class PickerOption(
    val url: String,
    val branch: String = "master",
    val language: String = "java",
    val baseDir: String = "datasets",
    val builderTypes: List<CodeContextStrategy> = listOf(
        CodeContextStrategy.SIMILAR_CHUNKS
    ),
    val codeQualityTypes: List<CodeQualityType> = listOf(),
    val builderConfig: BuilderConfig = BuilderConfig(),
) {
    fun pureDataFileName(): String {
        return baseDir + File.separator + repoFileName() + ".jsonl"
    }

    fun repoFileName() = "${url.replace("/", "_")}_${branch}_${language}.json"
}
