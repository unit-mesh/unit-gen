package cc.unitmesh.pick.threshold

import kotlinx.serialization.Serializable

@Serializable
data class FilterResult(
    val result: Boolean,
    val reason: String = "",
    val isCritical: Boolean = false,
)

interface Filter<T> {
    fun filter(data: T): FilterResult
}