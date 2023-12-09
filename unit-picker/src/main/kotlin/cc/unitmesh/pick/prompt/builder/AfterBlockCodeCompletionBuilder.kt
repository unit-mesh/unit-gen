package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import kotlinx.serialization.Serializable


@Serializable
data class AfterBlockCodeCompletionIns(
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
    val output: String,
)

class AfterBlockCodeCompletionBuilder(val context: InstructionContext) : InstructionBuilder<AfterBlockCodeCompletionIns> {
    val instruction: String = ""
    private val output: String = ""
    private val language: String = ""
    private val beforeCursorCode: String = ""
    private val afterCursorCode: String = ""

    override fun build(): List<AfterBlockCodeCompletionIns> {
        TODO("Not yet implemented")
    }

    override fun unique(list: List<AfterBlockCodeCompletionIns>): List<Instruction> {
        return listOf(Instruction(
            instruction,
            output = output,
            input = """```$language
                |$beforeCursorCode
                |$afterCursorCode
                |```""".trimMargin()
        ))
    }
}