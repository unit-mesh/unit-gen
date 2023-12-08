package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.pick.ext.toUml
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import chapi.domain.core.CodeDataStruct

class RelatedCodeCompletionBuilder(private val context: InstructionContext) : InstructionBuilder {
    private var language: String = ""

    override fun build(): List<Instruction> {
        language = context.job.fileSummary.language
        val container = context.job.container ?: return emptyList()

        // 1. collection all related data structure by imports if exists in a file tree
        val relatedDataStructure = container.Imports.mapNotNull {
            context.fileTree[it.Source]?.container?.DataStructures
        }.flatten()

        // 2. convert all related data structure to uml
        val relatedCode = relatedDataStructure.joinToString("\n", transform = CodeDataStruct::toUml)

        val map = container.DataStructures.map { ds ->
            ds.Functions.map {
                val position = it.Position
                val beforeCursor = context.job.codeLines.subList(0, position.StartLine).joinToString("\n")

                val stopLine = if (position.StopLine == 0) {
                    context.job.codeLines.size
                } else {
                    position.StopLine
                }

                val afterCursor = context.job.codeLines.subList(position.StartLine, stopLine).joinToString("\n")

                Instruction(
                    instruction = "Complete $language code, return rest code, no explaining",
                    output = afterCursor,
                    input = """
                |```$language
                |$relatedCode
                |```
                |
                |Code:
                |```$language
                |$beforeCursor
                |```""".trimMargin()
                )
            }
        }

        // convert completion for template to instruction

        return map.flatten()
    }
}

