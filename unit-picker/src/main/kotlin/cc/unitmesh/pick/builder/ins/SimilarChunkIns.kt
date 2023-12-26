package cc.unitmesh.pick.builder.ins

import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.Instruction
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.threshold.InsQualityThreshold
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SimilarChunkIns(
    val language: String,
    val beforeCursor: String,
    val afterCursor: String,
    val similarChunks: String,
    val output: String,
    override val type: CompletionBuilderType,
) : TypedIns {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }

    override fun unique(): Instruction {
        // Similar chunk strategy
        val similarChunks = if (similarChunks.isNotBlank() && similarChunks.isNotEmpty()) {
            // limit similarChunks to 30 lines
            val similarChunksLines = similarChunks.lines()
            val maxLine = InsQualityThreshold.MAX_RELATED_CODE_LINE
            if (similarChunksLines.size > maxLine) {
                similarChunksLines.take(maxLine).joinToString("\n")
            } else {
                similarChunks
            }
            "\n// Similar chunk:\n ```${language}\n${similarChunksLines.joinToString("\n")}\n```"
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

        val input = "$similarChunks\n\nCode:\n```${language}\n$beforeCursor\n```"

        return Instruction(
            instruction = "Complete $language code, return rest code, no explaining",
            output = output,
            input = input
        )
    }
}