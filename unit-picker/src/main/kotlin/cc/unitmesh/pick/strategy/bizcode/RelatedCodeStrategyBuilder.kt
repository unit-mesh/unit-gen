package cc.unitmesh.pick.strategy.bizcode

import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.core.intelli.SimilarChunker
import cc.unitmesh.pick.builder.completionBuilders
import cc.unitmesh.pick.strategy.ins.RelatedCodeIns
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct

/**
 * The `RelatedCodeStrategyBuilder` class is responsible for building a list of completion instructions for related code.
 * It implements the `CodeStrategyBuilder` interface.
 *
 * @property context The job context containing the necessary information for code completion.
 *
 * @constructor Creates a `RelatedCodeStrategyBuilder` with the specified job context.
 */
class RelatedCodeStrategyBuilder(private val context: JobContext) : CodeStrategyBuilder {
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

        val currentPath = container.DataStructures[0].FilePath

        // 2. get name to calculate similarity to take the most similar 3
        val findRelatedCodeDs = findRelatedCode(container)
        val relatedCodePath = findRelatedCodeDs.map { it.FilePath }
        val jaccardSimilarity = SimilarChunker.pathLevelJaccardSimilarity(relatedCodePath, currentPath)
        val relatedCode = jaccardSimilarity.mapIndexed { index, d ->
            findRelatedCodeDs[index] to d
        }.sortedByDescending {
            it.second
        }.take(3).map {
            it.first
        }

        // 3. build completion instruction
        val builders = completionBuilders(context.completionBuilderTypes, context)
        builders.asSequence().forEach {
            it.build(container)
        }

        val codeCompletionIns = dataStructs.map { ds ->
            val blockIns = builders.asSequence().map {
                it.build(ds)
            }.flatten()

            val functionsIns = ds.Functions.map { function ->
                builders.asSequence().map {
                    it.build(function)
                }
                    .flatten()
                    .filter {
                        it.afterCursor.isNotBlank() && it.beforeCursor.isNotBlank()
                    }
                    .take(context.maxTypedCompletionSize)
                    .map {
                        RelatedCodeIns(
                            language = language,
                            beforeCursor = it.beforeCursor,
                            relatedCode = relatedCode,
                            output = it.afterCursor,
                            type = it.completionBuilderType
                        )
                    }.toList()
            }.flatten()
            functionsIns + blockIns
        }.flatten()

        return codeCompletionIns
    }

    private fun findRelatedCode(container: CodeContainer): List<CodeDataStruct> {
        // 1. collects all similar data structure by imports if exists in a file tree
        val byImports = container.Imports
            .mapNotNull {
                context.fileTree[it.Source]?.container?.DataStructures
            }
            .flatten()

        // 2. collects by inheritance tree for some node in the same package
        val byInheritance = container.DataStructures
            .map {
                (it.Implements + it.Extend).mapNotNull { i ->
                    context.fileTree[i]?.container?.DataStructures
                }.flatten()
            }
            .flatten()

        val related = (byImports + byInheritance).distinctBy { it.NodeName }
        // 3. convert all similar data structure to uml
        return related
    }
}

