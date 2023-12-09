package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class InBlockCodeCompletionIns(
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
    val output: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}

class InBlockCodeCompletionBuilder(val context: InstructionContext) : InstructionBuilder<InBlockCodeCompletionIns> {
    val instruction: String = "";
    private val output: String = "";
    private val language: String = "";
    private val beforeCursorCode: String = "";
    private val afterCursorCode: String = "";

    override fun build(): List<InBlockCodeCompletionIns> {
        TODO("Not yet implemented")
    }

    override fun unique(list: List<InBlockCodeCompletionIns>): List<Instruction> {
        return listOf(
            Instruction(
                instruction,
                output = output,
                input = """```$language
                |$beforeCursorCode
                |$afterCursorCode
                |```""".trimMargin()
            )
        )
    }
}