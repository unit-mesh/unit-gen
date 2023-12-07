package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext

class AfterBlockCodeCompletionBuilder(val context: InstructionContext) : InstructionBuilder {
    val instruction: String = ""
    val output: String = ""
    val language: String = ""
    val beforeCursorCode: String = ""
    val afterCursorCode: String = ""

    override fun build(): List<Instruction> {
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