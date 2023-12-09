package cc.unitmesh.pick.config

import cc.unitmesh.pick.prompt.InstructionType
import cc.unitmesh.quality.CodeQualityType
import kotlinx.serialization.Serializable

@Serializable
data class PickerConfig(
    val url: String,
    val branch: String = "master",
    val language: String = "java",
    val baseDir: String = "datasets",
    val builderTypes: List<InstructionType> = listOf(
        InstructionType.RELATED_CODE_COMPLETION
    ),
    val codeQualityTypes: List<CodeQualityType> = listOf(),
    val builderConfig: BuilderConfig = BuilderConfig(),
)
