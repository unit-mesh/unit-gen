package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.ext.CodeDataStructUtil
import cc.unitmesh.pick.ext.toUml
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext

class RelatedCodeCompletionBuilder(private val context: InstructionContext) : InstructionBuilder {
    private var language: String = "";

    override fun build(): List<Instruction> {
        language = context.job.fileSummary.language
        val container = context.job.container ?: return emptyList()

        val relatedDataStructure = container.Imports.mapNotNull {
            context.fileTree[it.Source]?.container?.DataStructures
        }.flatten()

        val relatedCode = relatedDataStructure.joinToString("\n") {
            it.toUml()
        }

        return container.DataStructures.map { ds ->
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
        }.flatten()
    }
}

