package cc.unitmesh.pick.strategy.bizcode

import cc.unitmesh.core.completion.TypedIns
import cc.unitmesh.pick.builder.completionBuilders
import cc.unitmesh.pick.builder.ins.RelatedCodeIns
import cc.unitmesh.pick.builder.unittest.lang.UnitTestService
import cc.unitmesh.pick.strategy.base.CodeStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct

class RelatedCodeStrategyBuilder(private val context: JobContext) : CodeStrategyBuilder {

    override fun build(): List<TypedIns> {
        val language = context.job.fileSummary.language.lowercase()
        val container = context.job.container ?: return emptyList()
        val relatedCode = findRelatedCode(container)

        // 3. checks with rule specified in config
        val dataStructs = container.DataStructures.filter {
            hasIssue(it, context.qualityTypes)
        }

        if (dataStructs.isEmpty()) {
            return emptyList()
        }

        val builders = completionBuilders(context.completionBuilderTypes, context)

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
                    .take(context.maxCompletionInOneFile)
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

