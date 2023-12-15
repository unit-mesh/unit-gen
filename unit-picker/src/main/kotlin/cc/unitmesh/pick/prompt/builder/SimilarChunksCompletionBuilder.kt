package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import cc.unitmesh.pick.related.JavaSimilarChunks
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SimilarChunkCompletionIns(
    val language: String,
    val beforeCursor: String,
    val afterCursor: String,
    val similarChunks: List<String>,
    val output: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}

class SimilarChunksCompletionBuilder(private val context: InstructionContext) :
    InstructionBuilder<SimilarChunkCompletionIns> {
    override fun build(): List<SimilarChunkCompletionIns> {
        val language = context.job.fileSummary.language.lowercase()
        val container = context.job.container ?: return emptyList()

        // 1. collection all similar chunk structures by imports if exists in a file tree
        val similarChunks: List<String> =
            JavaSimilarChunks(context.fileTree).calculate(context.job.code).chunks ?: emptyList()

        // 3. checks with rule specified in config
        val dataStructs = container.DataStructures.filter {
            hasIssue(it, context.qualityTypes)
        }

        if (dataStructs.isEmpty()) {
            return emptyList()
        }

        val codeCompletionIns = dataStructs.map { ds ->
            ds.Functions.map {
                val position = it.Position
                val beforeCursor = context.job.codeLines.subList(0, position.StartLine).joinToString("\n")

                val stopLine = if (position.StopLine == 0) {
                    context.job.codeLines.size
                } else {
                    position.StopLine
                }

                val afterCursor = context.job.codeLines.subList(position.StartLine, stopLine).joinToString("\n")

                if (afterCursor.isBlank() || beforeCursor.isBlank()) {
                    return@map null
                }

                SimilarChunkCompletionIns(
                    language = language,
                    beforeCursor = beforeCursor,
                    similarChunks = similarChunks,
                    afterCursor = afterCursor,
                    output = afterCursor
                )
            }.filterNotNull()
        }.flatten()

        return codeCompletionIns
    }

    override fun unique(list: List<SimilarChunkCompletionIns>): List<Instruction> {
        return list.map {
            Instruction(
                instruction = "Complete ${it.language} code, return rest code, no explaining",
                output = it.output,
                input = """
                |```${it.language}
                |${it.similarChunks.joinToString("\n")}
                |```
                |
                |Code:
                |```${it.language}
                |${it.beforeCursor}
                |```""".trimMargin()
            )
        }
    }
}