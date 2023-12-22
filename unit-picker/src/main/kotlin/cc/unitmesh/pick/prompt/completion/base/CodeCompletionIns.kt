package cc.unitmesh.pick.prompt.completion.base

import cc.unitmesh.pick.prompt.completion.CompletionBuilderType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CodeCompletionIns(
    val beforeCursor: String,
    val afterCursor: String,
    val completionBuilderType: CompletionBuilderType,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}