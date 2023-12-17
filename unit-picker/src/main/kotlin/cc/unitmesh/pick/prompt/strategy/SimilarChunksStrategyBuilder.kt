package cc.unitmesh.pick.prompt.strategy

import cc.unitmesh.pick.prompt.*
import cc.unitmesh.pick.similar.JavaSimilarChunker
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

class SimilarChunksStrategyBuilder(private val context: JobContext) :
    CodeStrategyBuilder<SimilarChunkCompletionIns> {
    override fun build(): List<SimilarChunkCompletionIns> {
        val language = context.job.fileSummary.language.lowercase()
        val container = context.job.container ?: return emptyList()

        // 1. checks with rule specified in config
        val dataStructs = container.DataStructures.filter {
            hasIssue(it, context.qualityTypes)
        }

        if (dataStructs.isEmpty()) {
            return emptyList()
        }

        val similarChunker = JavaSimilarChunker(context.fileTree)
        val builders = completionBuilders(context.completionBuilderTypes, context)

        // 2. collect all with similar data structure
        val codeCompletionIns = dataStructs.map { ds ->
            ds.Functions.map { function ->
                builders.map { it.build(function) }
                    .flatten()
                    .filter {
                        it.afterCursor.isNotBlank() && it.beforeCursor.isNotBlank()
                    }.map {
                        val similarChunks: List<String> = similarChunker.calculate(
                            it.beforeCursor,
                            ds.Package + "." + ds.NodeName,
                        ).chunks ?: emptyList()

                        SimilarChunkCompletionIns(
                            language = language,
                            beforeCursor = it.beforeCursor,
                            similarChunks = similarChunks,
                            afterCursor = it.afterCursor,
                            output = it.afterCursor
                        )
                    }
            }.flatten()
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