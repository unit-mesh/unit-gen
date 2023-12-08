package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import kotlinx.serialization.Serializable

@Serializable
data class InlineCodeCompletionIns(
    val language: String,
    val beforeCursorCode: String,
    val output: String,
)

class InlineCodeCompletionBuilder(val context: InstructionContext) : InstructionBuilder<InlineCodeCompletionIns> {
    val instruction: String = "";
    private val output: String = "";
    private val language: String = "";
    private val beforeCursorCode: String = "";
    override fun convert(): List<InlineCodeCompletionIns> {
        TODO("Not yet implemented")
    }

    override fun build(): List<Instruction> {
        return listOf(Instruction(
            instruction,
            output = output,
            input = """```$language
                |$beforeCursorCode
                |```""".trimMargin()
        ))
    }
}