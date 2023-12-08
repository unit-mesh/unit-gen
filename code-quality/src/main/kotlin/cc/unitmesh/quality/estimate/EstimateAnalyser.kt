package cc.unitmesh.quality.estimate

import chapi.domain.core.CodeDataStruct
import org.archguard.scanner.analyser.count.FileJob
import org.archguard.scanner.analyser.count.LanguageSummary
import org.archguard.scanner.analyser.count.LanguageWorker
import java.nio.file.Path

class EstimateAnalyser {

    private var languageWorker = LanguageWorker()

    fun analysisByNode(node: CodeDataStruct): LanguageSummary? {
        val summary = analysisByContent(node.Content, Path.of(node.FilePath).fileName.toString())
        return summary?.complexity?.let {
            if (it > 0) {
                summary
            } else {
                null
            }
        }
    }

    fun analysisByContent(content: String, filename: String): LanguageSummary? {
        val fileContent = content.toByteArray()
        val fileJob = FileJob(
            language = "Go",
            content = fileContent,
            filename = filename,
            bytes = fileContent.size.toLong(),
        )


        val countStats = languageWorker.countStats(fileJob) ?: return null

        return LanguageSummary(
            name = countStats.language,
            lines = countStats.lines,
            code = countStats.code,
            blank = countStats.blank,
            comment = countStats.comment,
            complexity = countStats.complexity,
            weightedComplexity = countStats.weightedComplexity,
            bytes = countStats.bytes,
        )
    }

    companion object {
        private var instance: EstimateAnalyser? = null
        fun getInstance(): EstimateAnalyser {
            if (instance == null) {
                instance = EstimateAnalyser()
            }

            return instance!!
        }
    }
}