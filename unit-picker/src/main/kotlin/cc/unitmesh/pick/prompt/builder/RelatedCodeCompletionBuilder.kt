package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.ext.toUml
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RelatedCodeCompletionIns(
    val language: String,
    val beforeCursor: String,
    val relatedCode: String,
    val output: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}

class RelatedCodeCompletionBuilder(private val context: InstructionContext) :
    InstructionBuilder<RelatedCodeCompletionIns> {

    override fun build(): List<RelatedCodeCompletionIns> {
        val language = context.job.fileSummary.language.lowercase()
        val container = context.job.container ?: return emptyList()

        // 1. collection all related data structure by imports if exists in a file tree
        val relatedDataStructure = container.Imports
            .mapNotNull {
                context.fileTree[it.Source]?.container?.DataStructures
            }
            .flatten()

        // 2. convert all related data structure to uml
        val relatedCode = relatedDataStructure.joinToString("\n", transform = CodeDataStruct::toUml)

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

                RelatedCodeCompletionIns(
                    language = language,
                    beforeCursor = beforeCursor,
                    relatedCode = relatedCode,
                    output = afterCursor
                )
            }.filterNotNull()
        }.flatten()

        return codeCompletionIns
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

