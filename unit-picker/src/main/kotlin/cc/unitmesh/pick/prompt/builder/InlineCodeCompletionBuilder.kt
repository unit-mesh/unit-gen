package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.CodeContextBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class InlineCodeCompletionIns(
    val language: String,
    val beforeCursorCode: String,
    val output: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}

class InlineCodeCompletionBuilder(val context: InstructionContext) : CodeContextBuilder<InlineCodeCompletionIns> {
    val instruction: String = "";
    private val output: String = "";
    private val language: String = "";
    private val beforeCursorCode: String = "";
    override fun build(): List<InlineCodeCompletionIns> {
        TODO("Not yet implemented")
    }

    override fun unique(list: List<InlineCodeCompletionIns>): List<Instruction> {
        return listOf(Instruction(
            instruction,
            output = output,
            input = """```$language
                |$beforeCursorCode
                |```""".trimMargin()
        ))
    }
}