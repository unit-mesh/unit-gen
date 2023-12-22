package cc.unitmesh.quality.badsmell

import kotlinx.serialization.Serializable

@Serializable
data class BsThresholds(
    val bsLongParasLength: Int = 5,
    val bsIfSwitchLength: Int = 8,
    val bsLargeLength: Int = 20,
    val bsMethodLength: Int = 30,
    val bsIfLinesLength: Int = 3,
) {
    fun toThresholds(): Map<String, Int> {
        return mapOf(
            "bsLongParasLength" to bsLongParasLength,
            "bsIfSwitchLength" to bsIfSwitchLength,
            "bsLargeLength" to bsLargeLength,
            "bsMethodLength" to bsMethodLength,
            "bsIfLinesLength" to bsIfLinesLength,
        )
    }

    fun from(thresholds: Map<String, Int>): BsThresholds {
        return BsThresholds(
            thresholds["bsLongParasLength"] ?: bsLongParasLength,
            thresholds["bsIfSwitchLength"] ?: bsIfSwitchLength,
            thresholds["bsLargeLength"] ?: bsLargeLength,
            thresholds["bsMethodLength"] ?: bsMethodLength,
            thresholds["bsIfLinesLength"] ?: bsIfLinesLength,
        )
    }
}