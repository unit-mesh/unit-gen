package cc.unitmesh.core.comment

import chapi.domain.core.CodePosition
import kotlinx.serialization.Serializable

@Serializable
data class CodeComment(
    val content: String,
    val position: CodePosition
)