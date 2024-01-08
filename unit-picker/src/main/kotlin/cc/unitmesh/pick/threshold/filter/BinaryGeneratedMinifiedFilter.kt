package cc.unitmesh.pick.threshold.filter

import cc.unitmesh.pick.threshold.pipeline.Filter
import cc.unitmesh.pick.threshold.pipeline.FilterResult
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.FileSummary

class BinaryGeneratedMinifiedFilter(qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    override fun filter(data: FileSummary): FilterResult {
        return FilterResult(!(data.binary || data.generated || data.minified), "binary or generated or minified", true)
    }
}