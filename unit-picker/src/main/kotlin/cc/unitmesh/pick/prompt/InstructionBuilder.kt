package cc.unitmesh.pick.prompt

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CodeCompletionIns(
    val language: String,
    val beforeCursor: String,
    val afterCursor: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}

interface InstructionBuilder {
    fun build(context: JobContext): List<CodeCompletionIns>
}


