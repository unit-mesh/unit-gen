package cc.unitmesh.pick.threshold.filter

import cc.unitmesh.pick.threshold.Filter
import cc.unitmesh.pick.threshold.FilterResult
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.FileSummary
import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.Encoding
import com.knuddels.jtokkit.api.EncodingRegistry
import com.knuddels.jtokkit.api.EncodingType

class TokenLengthFilter(private val qualityThreshold: InsQualityThreshold) : Filter<FileSummary> {
    private var registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
    private var enc: Encoding = registry.getEncoding(EncodingType.CL100K_BASE)

    override fun filter(data: FileSummary): FilterResult {
        val encoded = enc.encode(data.content.toString())
        val length = encoded.size
        val codeWithBuffer = 1.25
        return FilterResult(
            length <= qualityThreshold.maxTokenLength * codeWithBuffer,
            "token length too long: $length",
            true
        )
    }
}