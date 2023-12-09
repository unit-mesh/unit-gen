package cc.unitmesh.pick.config

import kotlinx.serialization.Serializable

/**
 * For different generic data in [cc.unitmesh.pick.prompt.InstructionBuilder]
 */
@Serializable
data class BuilderConfig(
    val withGenTempData: Boolean = true,
    val mergeFinalOutput: Boolean = true,
)