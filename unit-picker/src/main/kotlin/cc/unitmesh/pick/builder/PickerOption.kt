package cc.unitmesh.pick.builder

import cc.unitmesh.pick.prompt.CodeContextStrategy
import cc.unitmesh.pick.prompt.CompletionBuilderType
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.quality.CodeQualityType
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class PickerOption(
    val url: String,
    val branch: String = "master",
    val language: String = "java",
    val baseDir: String = "datasets",
    val codeContextStrategies: List<CodeContextStrategy> = listOf(
        CodeContextStrategy.RELATED_CODE
    ),
    val completionTypes: List<CompletionBuilderType> = listOf(
        CompletionBuilderType.AFTER_BLOCK_COMPLETION,
        CompletionBuilderType.IN_BLOCK_COMPLETION,
    ),
    val codeQualityTypes: List<CodeQualityType> = listOf(),
    val builderConfig: BuilderConfig = BuilderConfig(),
) {
    fun pureDataFileName(): String {
        return baseDir + File.separator + repoFileName() + ".jsonl"
    }

    fun repoFileName() = "${url.replace("/", "_")}_${branch}_${language}.json"
}
