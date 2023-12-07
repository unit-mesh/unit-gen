package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext

class RelatedCodeCompletionBuilder(val context: InstructionContext) : InstructionBuilder {
    val instruction: String = "";
    val output: String = "";
    val language: String = "";
    val beforeCursorCode: String = "";
    val relatedCode: String = "";

    override fun build(): List<Instruction> {
        return listOf(Instruction(
            instruction,
            output = output,
            input = """
                | Compare this snippet:
                |```$language
                |$relatedCode
                |```
                |Code:
                |```$language
                |$beforeCursorCode
                |```""".trimIndent()
        ))
    }


}