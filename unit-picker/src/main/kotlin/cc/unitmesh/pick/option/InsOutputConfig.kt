package cc.unitmesh.pick.option

import kotlinx.serialization.Serializable


@Serializable
data class InsOutputConfig(
    /**
     * For different generic data in [cc.unitmesh.pick.prompt.CodeStrategyBuilder]
     */
    val withGenPureData: Boolean = true,
    val mergeFinalOutput: Boolean = true,
)