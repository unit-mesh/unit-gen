package cc.unitmesh.core.ast

import kotlinx.serialization.Serializable

enum class NodeType {
    CLASS,
    METHOD,
    ;
}

/**
 * 用于标识一个 [CodeDataStruct] 中的 Function 或者 Class，以在生成指令时更有标志性。
 */
@Serializable
data class NodeIdentifier(
    val type: NodeType,
    val name: String,
)