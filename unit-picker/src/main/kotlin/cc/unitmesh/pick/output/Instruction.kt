package cc.unitmesh.pick.output

enum class InstructionType {
    INLINE_CODE_COMPLETION,
    IN_BLOCK_CODE_COMPLETION,
    AFTER_BLOCK_CODE_COMPLETION,
    RELATED_CODE_COMPLETION,
//    CODE_DIFF,
//    REFACTOR,
}

sealed class Instruction(
    val instructionType: InstructionType,
    val instruction: String,
    val output: String,
) {
    abstract fun input(): String
}

class InlineCodeCompletion(
    instruction: String,
    output: String,
    val language: String,
    val beforeCursorCode: String,
) : Instruction(InstructionType.INLINE_CODE_COMPLETION, instruction, output) {
    override fun input(): String {
        return """```$language
            |$beforeCursorCode
            |```""".trimMargin()
    }
}

class InBlockCodeCompletion(
    instruction: String,
    output: String,
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
) : Instruction(InstructionType.IN_BLOCK_CODE_COMPLETION, instruction, output) {
    override fun input(): String {
        return """```$language
            |$beforeCursorCode
            |$afterCursorCode
            |```""".trimMargin()
    }
}

class AfterBlockCodeCompletion(
    instruction: String,
    output: String,
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
) : Instruction(InstructionType.AFTER_BLOCK_CODE_COMPLETION, instruction, output) {
    override fun input(): String {
        return """```$language
            |$beforeCursorCode
            |$afterCursorCode
            |```""".trimMargin()
    }
}

class RelatedCodeCompletion(
    instruction: String,
    output: String,
    val language: String,
    val beforeCursorCode: String,
    val relatedCode: String,
) : Instruction(InstructionType.RELATED_CODE_COMPLETION, instruction, output) {
    override fun input(): String {
        return """
            | Compare this snippet:
            |```$language
            |$relatedCode
            |```
            |Code:
            |```$language
            |$beforeCursorCode
            |```""".trimMargin()
    }
}

