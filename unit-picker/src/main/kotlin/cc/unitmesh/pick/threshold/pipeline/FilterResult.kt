package cc.unitmesh.pick.threshold.pipeline

import kotlinx.serialization.Serializable

@Serializable
data class FilterResult(
    val result: Boolean,
    val reason: String = "",
    val isCritical: Boolean = false,
)