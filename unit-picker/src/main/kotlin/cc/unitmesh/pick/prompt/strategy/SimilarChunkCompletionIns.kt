package cc.unitmesh.pick.prompt.strategy

import cc.unitmesh.pick.prompt.CompletionBuilderType
import cc.unitmesh.pick.prompt.Instruction
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SimilarChunkCompletionIns(
    val language: String,
    val beforeCursor: String,
    val afterCursor: String,
    val similarChunks: String,
    val output: String,
    override val type: CompletionBuilderType,
) : TypedCompletionIns {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }

    override fun unique(): Instruction {
        val input = "\n${similarChunks}              \nCode:\n```${language}\n${beforeCursor}\n```"

        return Instruction(
            instruction = "Complete $language code, return rest code, no explaining",
            output = output,
            input = input
        )
    }
}