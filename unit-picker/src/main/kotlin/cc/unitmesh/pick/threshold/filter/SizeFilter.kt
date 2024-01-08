package cc.unitmesh.pick.threshold.filter

import cc.unitmesh.pick.threshold.Filter
import cc.unitmesh.pick.threshold.FilterResult
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.FileSummary

class SizeFilter(private val qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    override fun filter(data: FileSummary): FilterResult {
        return FilterResult(data.bytes <= qualityThreshold.fileSize, "file size too large: ${data.bytes}", true)
    }
}