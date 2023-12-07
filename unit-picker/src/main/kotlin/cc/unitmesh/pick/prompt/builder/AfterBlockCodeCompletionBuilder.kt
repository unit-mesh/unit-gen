package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext

class AfterBlockCodeCompletionBuilder(
    val instruction: String,
    val output: String,
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
) : InstructionBuilder {
    override fun build(context: InstructionContext): Instruction {
        return Instruction(
            instruction,
            output = output,
            input = """```$language
                |$beforeCursorCode
                |$afterCursorCode
                |```""".trimMargin()
        )
    }
}