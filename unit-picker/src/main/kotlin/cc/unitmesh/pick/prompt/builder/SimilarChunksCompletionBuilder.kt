package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SimilarChunkCompletionIns(
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
    val similarChunks: List<String>,
    val output: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}

class SimilarChunksCompletionBuilder(private val context: InstructionContext) :
    InstructionBuilder<SimilarChunkCompletionIns> {
    override fun build(): List<SimilarChunkCompletionIns> {
        TODO("Not yet implemented")
    }

    override fun unique(list: List<SimilarChunkCompletionIns>): List<Instruction> {
        TODO("Not yet implemented")
    }
}