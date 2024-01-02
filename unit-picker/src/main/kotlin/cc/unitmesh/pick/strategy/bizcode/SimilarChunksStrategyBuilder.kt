package cc.unitmesh.pick.strategy.bizcode

import cc.unitmesh.core.SupportedLang
import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.core.intelli.SimilarChunker
import cc.unitmesh.pick.builder.completionBuilders
import cc.unitmesh.pick.similar.JavaSimilarChunker
import cc.unitmesh.pick.similar.TypeScriptSimilarChunker
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.strategy.ins.SimilarChunkIns
import cc.unitmesh.pick.worker.job.JobContext

class SimilarChunksStrategyBuilder(private val context: JobContext) : CodeStrategyBuilder {
    /**
     * Builds a list of SimilarChunkIns objects.
     *
     * This method is responsible for generating a list of SimilarChunkIns objects based on the provided context,
     * container, and rule configuration. It performs the following steps:
     *
     * 1. Checks with the rule specified in the configuration to identify data structures with issues.
     * 2. Collects all data structures with similar data structure.
     * 3. Builds completion builders for each function in the identified data structures.
     * 4. Filters out completion builders with empty before and after cursors.
     * 5. Calculates similar chunks for block completions using the JavaSimilarChunker.
     * 6. Creates SimilarChunkIns objects for each completion builder, with relevant information on language, before cursor,
     *    similar chunks, after cursor, output, and type.
     * 7. Returns the list of generated SimilarChunkIns objects.
     *
     * @return a list of SimilarChunkIns objects representing code completions with similar chunks
     */
    override fun build(): List<TypedIns> {
        val language = context.job.fileSummary.language.lowercase()
        val container = context.job.container ?: return emptyList()

        // 1. checks with rule specified in config
        val dataStructs = container.DataStructures.filter {
            hasIssue(it, context.qualityTypes)
        }

        if (dataStructs.isEmpty()) {
            return emptyList()
        }

        val similarChunker: SimilarChunker = when(context.project.language) {
            SupportedLang.JAVA -> JavaSimilarChunker(context.fileTree)
            SupportedLang.KOTLIN -> JavaSimilarChunker(context.fileTree)
            SupportedLang.TYPESCRIPT -> TypeScriptSimilarChunker(context.fileTree)
            SupportedLang.RUST -> TODO()
        }

        val builders = completionBuilders(context.completionBuilderTypes, context)
        val containerIns = builders.asSequence().map {
            it.build(container)
        }.flatten()

        // 2. collect all with similar data structure
        val codeCompletionIns = dataStructs.map { ds ->
            val canonicalName = ds.Package + "." + ds.NodeName

            ds.Functions.map { function ->
                builders.map { it.build(function) }
                    .flatten()
                    .filter {
                        it.afterCursor.isNotBlank() && it.beforeCursor.isNotBlank()
                    }.mapNotNull {
                        val isBlockCompletion = it.afterCursor.length > 10
                        val similarChunks: String = if (isBlockCompletion) {
                            similarChunker.calculate(it.beforeCursor, canonicalName).format()
                        } else {
                            ""
                        }

                        if (similarChunks.isBlank()) {
                            return@mapNotNull null
                        }

                        SimilarChunkIns(
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

        return codeCompletionIns + containerIns
    }
}