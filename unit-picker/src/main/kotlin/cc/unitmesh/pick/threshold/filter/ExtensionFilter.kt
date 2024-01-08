package cc.unitmesh.pick.threshold.filter

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.pick.threshold.pipeline.Filter
import cc.unitmesh.pick.threshold.pipeline.FilterResult
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.FileSummary
import org.archguard.scanner.analyser.count.LanguageService

class ExtensionFilter(private val qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    private val language: LanguageService = LanguageService()

    private val supportedExtensions: Set<String> = SupportedLang.all().map {
        language.getExtension(it.extension)
    }.toSet()

    override fun filter(data: FileSummary): FilterResult {
        val ext = data.extension
        return FilterResult(supportedExtensions.contains(ext), "extension not supported: $ext")
    }
}