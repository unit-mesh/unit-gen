package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import kotlinx.serialization.Serializable

@Serializable
class InlineCodeCompletionBuilder(
    val instruction: String,
    val output: String,
    val language: String,
    val beforeCursorCode: String,
) : InstructionBuilder {
    override fun build(context: InstructionContext): Instruction {
        return Instruction(
            instruction,
            output = output,
            input = """```$language
                |$beforeCursorCode
                |```""".trimMargin()
        )
    }
}