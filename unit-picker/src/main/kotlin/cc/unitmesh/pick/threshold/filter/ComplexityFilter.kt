package cc.unitmesh.pick.threshold.filter

import cc.unitmesh.pick.threshold.Filter
import cc.unitmesh.pick.threshold.FilterResult
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.FileSummary

class ComplexityFilter(private val qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    override fun filter(data: FileSummary): FilterResult {
        return FilterResult(
            data.complexity <= qualityThreshold.complexity,
            "complexity too high: ${data.complexity}",
            true
        )
    }
}