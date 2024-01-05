package cc.unitmesh.pick.worker

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.strategy.CodeStrategyType
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.option.InsPickerOption
import cc.unitmesh.pick.project.ProjectContext
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.quality.CodeQualityType
import kotlinx.serialization.Serializable
import org.archguard.scanner.core.sca.CompositionDependency
import org.jetbrains.annotations.TestOnly
import java.io.File

@Serializable
data class WorkerContext(
    val codeContextStrategies: List<CodeStrategyType>,
    val qualityTypes: List<CodeQualityType>,
    val insOutputConfig: InsOutputConfig,
    val pureDataFileName: String,
    val completionTypes: List<CompletionBuilderType>,
    val maxCompletionInOneFile: Int,
    val completionTypeSize: Int,
    val qualityThreshold: InsQualityThreshold = InsQualityThreshold(),
    var compositionDependency: List<CompositionDependency> = listOf(),
    val project: ProjectContext,
) {
    companion object {
        @TestOnly
        fun default(): WorkerContext {
            return WorkerContext(
                codeContextStrategies = listOf(),
                qualityTypes = listOf(),
                insOutputConfig = InsOutputConfig(),
                pureDataFileName = "",
                completionTypes = listOf(),
                maxCompletionInOneFile = 0,
                completionTypeSize = 0,
                qualityThreshold = InsQualityThreshold(),
                compositionDependency = listOf(),
                project = ProjectContext()
            )
        }

        fun fromConfig(language: SupportedLang, insPickerOption: InsPickerOption, codeDir: File): WorkerContext {
            return WorkerContext(
                insPickerOption.codeStrategyTypes,
                insPickerOption.codeQualityTypes,
                insPickerOption.insOutputConfig,
                pureDataFileName = insPickerOption.pureDataFileName(),
                insPickerOption.completionTypes,
                insPickerOption.maxCompletionEachFile,
                insPickerOption.completionTypeSize,
                qualityThreshold = InsQualityThreshold(
                    complexity = InsQualityThreshold.MAX_COMPLEXITY,
                    fileSize = InsQualityThreshold.MAX_FILE_SIZE,
                    maxLineInCode = insPickerOption.maxLineInCode,
                    maxCharInCode = insPickerOption.maxCharInCode,
                    maxTokenLength = insPickerOption.maxTokenLength,
                ),
                project = ProjectContext(
                    language = language,
                    codeDir = codeDir.absolutePath
                )
            )

        }
    }
}
