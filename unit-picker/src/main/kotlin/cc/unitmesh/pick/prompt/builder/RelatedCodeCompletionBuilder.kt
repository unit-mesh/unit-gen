package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import kotlinx.serialization.Serializable

@Serializable
data class RelatedCodeCompletionBuilder(
    val instruction: String,
    val output: String,
    val language: String,
    val beforeCursorCode: String,
    val relatedCode: String,
) : InstructionBuilder {
    override fun build(context: InstructionContext): Instruction {
        return Instruction(
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
                |```""${'"'}.trimMargin()
            """.trimIndent()
        )
    }


}