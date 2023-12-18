package cc.unitmesh.pick.prompt.strategy

import cc.unitmesh.pick.ext.toUml
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.CodeStrategyBuilder
import cc.unitmesh.pick.prompt.JobContext
import cc.unitmesh.pick.prompt.completionBuilders
import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RelatedCodeCompletionIns(
    val language: String,
    val beforeCursor: String,
    val relatedCode: String,
    // the output aka afterCursor
    val output: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}

class RelatedCodeStrategyBuilder(private val context: JobContext) :
    CodeStrategyBuilder<RelatedCodeCompletionIns> {

    override fun build(): List<RelatedCodeCompletionIns> {
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
            ds.Functions.map { function ->
                builders.map { it.build(function) }
                    .flatten()
                    .filter {
                        it.afterCursor.isNotBlank() && it.beforeCursor.isNotBlank()
                    }.map {
                        RelatedCodeCompletionIns(
                            language = language,
                            beforeCursor = it.beforeCursor,
                            relatedCode = relatedCode,
                            output = it.afterCursor
                        )
                    }
            }.flatten()
        }.flatten()

        return codeCompletionIns
    }

    fun findRelatedCode(container: CodeContainer): String {
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
        val relatedCode = related.joinToString("\n", transform = CodeDataStruct::toUml)
        return relatedCode
    }

    override fun unique(list: List<RelatedCodeCompletionIns>): List<Instruction> {
        return list.map {
            Instruction(
                instruction = "Complete ${it.language} code, return rest code, no explaining",
                output = it.output,
                input = """
                |```${it.language}
                |${it.relatedCode}
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

