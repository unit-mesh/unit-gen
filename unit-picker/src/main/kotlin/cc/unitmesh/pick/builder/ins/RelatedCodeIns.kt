package cc.unitmesh.pick.builder.ins

import cc.unitmesh.pick.ext.toUml
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.Instruction
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.option.InsQualityThreshold
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RelatedCodeIns(
    val language: String,
    val beforeCursor: String,
    val relatedCode: List<CodeDataStruct>,
    // the output aka afterCursor
    val output: String,
    override val type: CompletionBuilderType,
) : TypedIns {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }

    override fun unique(): Instruction {
        // Related code strategy
        val relatedCode = if (relatedCode.isNotEmpty()) {
            // todo: count be similar
            val relatedCode = relatedCode.take(3).joinToString("\n") {
                it.toUml()
            }

            val relatedCodeLines = relatedCode.lines()
            val maxLine = InsQualityThreshold.MAX_RELATED_CODE_LINE
            if (relatedCodeLines.size > maxLine) {
                relatedCodeLines.take(maxLine).joinToString("\n")
            } else {
                relatedCode
            }
            "\n// Compare this snippets: \n ```${language}\n${relatedCodeLines.joinToString("\n")}\n```"
        } else {
            ""
        }

        // Count strategy
        val maxLine = InsQualityThreshold.MAX_LINE_IN_CODE
        val beforeCursorLine = beforeCursor.count { it == '\n' }
        val afterCursorLine = output.count { it == '\n' }
        // drop from the start of beforeCursor
        val beforeCursor = if (beforeCursorLine + afterCursorLine > maxLine) {
            val dropLine = beforeCursorLine + afterCursorLine - maxLine
            beforeCursor.lines().drop(dropLine).joinToString("\n")
        } else {
            beforeCursor
        }

        val input = "$relatedCode\n\nCode:\n```${language}\n$beforeCursor\n```"
        return Instruction(
            instruction = "Complete $language code, return rest code, no explaining",
            output = output,
            input = input
        )
    }

}