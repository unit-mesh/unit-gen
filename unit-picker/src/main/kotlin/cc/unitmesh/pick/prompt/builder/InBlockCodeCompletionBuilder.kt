package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import kotlinx.serialization.Serializable


@Serializable
data class InBlockCodeCompletionIns(
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
    val output: String,
)

class InBlockCodeCompletionBuilder(val context: InstructionContext) : InstructionBuilder<InBlockCodeCompletionIns> {
    val instruction: String = "";
    private val output: String = "";
    private val language: String = "";
    private val beforeCursorCode: String = "";
    private val afterCursorCode: String = "";

    override fun convert(): List<InBlockCodeCompletionIns> {
        TODO("Not yet implemented")
    }

    override fun build(): List<Instruction> {
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