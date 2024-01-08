package cc.unitmesh.pick.threshold.filter

import cc.unitmesh.pick.threshold.pipeline.Filter
import cc.unitmesh.pick.threshold.pipeline.FilterResult
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.FileSummary

/**
 * The `ComplexityFilter` class is responsible for filtering `FileSummary` objects based on their complexity.
 * It implements the `Filter` interface and provides the logic to determine if a file's complexity meets the threshold criteria.
 *
 * @param qualityThreshold The quality threshold object that contains the maximum allowed complexity.
 */
class ComplexityFilter(private val qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    override fun filter(data: FileSummary): FilterResult {
        return FilterResult(
            data.complexity <= qualityThreshold.complexity,
            "complexity too high: ${data.complexity}",
            true
        )
    }
}