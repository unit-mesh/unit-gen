package cc.unitmesh.pick.config

import kotlinx.serialization.Serializable


@Serializable
data class BuilderConfig(
    /**
     * For different generic data in [cc.unitmesh.pick.prompt.InstructionBuilder]
     */
    val withGenPureData: Boolean = true,
    val mergeFinalOutput: Boolean = true,
)