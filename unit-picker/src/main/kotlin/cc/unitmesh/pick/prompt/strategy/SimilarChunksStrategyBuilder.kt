package cc.unitmesh.pick.prompt.strategy

import cc.unitmesh.pick.prompt.*
import cc.unitmesh.pick.similar.JavaSimilarChunker

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
            val canonicalName = ds.Package + "." + ds.NodeName

            ds.Functions.map { function ->
                builders.map { it.build(function) }
                    .flatten()
                    .filter {
                        it.afterCursor.isNotBlank() && it.beforeCursor.isNotBlank()
                    }.mapNotNull {
                        val isBlockCompletion = it.afterCursor.length > 50
                        val similarChunks: String = if (isBlockCompletion) {
                            similarChunker.calculate(it.beforeCursor, canonicalName).format()
                        } else {
                            ""
                        }

                        if (similarChunks.isBlank()) {
                            return@mapNotNull null
                        }

                        SimilarChunkCompletionIns(
                            language = language,
                            beforeCursor = it.beforeCursor,
                            similarChunks = similarChunks,
                            afterCursor = it.afterCursor,
                            output = it.afterCursor,
                            type = it.completionBuilderType
                        )
                    }
            }.flatten()
        }.flatten()

        return codeCompletionIns
    }
}