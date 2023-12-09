package cc.unitmesh.pick.config

import cc.unitmesh.pick.prompt.InstructionType
import cc.unitmesh.quality.CodeQualityType
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class PickerOption(
    val url: String,
    val branch: String = "master",
    val language: String = "java",
    val baseDir: String = "datasets",
    val builderTypes: List<InstructionType> = listOf(
        InstructionType.RELATED_CODE_COMPLETION
    ),
    val codeQualityTypes: List<CodeQualityType> = listOf(),
    val builderConfig: BuilderConfig = BuilderConfig(),
) {
    fun pureDataFileName(): String {
        return baseDir + File.separator + repoFileName() + ".jsonl"
    }

    fun repoFileName() = "${url.replace("/", "_")}_${branch}_${language}.json"
}
