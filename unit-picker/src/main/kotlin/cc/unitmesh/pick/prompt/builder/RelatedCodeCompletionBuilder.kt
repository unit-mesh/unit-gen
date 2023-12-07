package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.ext.toUml
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext

class RelatedCodeCompletionBuilder(private val context: InstructionContext) : InstructionBuilder {
    private val instruction: String = "";
    private val output: String = "";
    private val language: String = "";
    private val beforeCursorCode: String = "";
    private val relatedCode: String = "";

    override fun build(): List<Instruction> {
        val container = context.job.container ?: return emptyList()

        val relatedDataStructure = container.Imports.mapNotNull {
            context.fileTree[it.Source]?.container?.DataStructures
        }.flatten()

        val relatedCode = relatedDataStructure.joinToString("\n") {
            it.toUml()
        }

//        container.DataStructures.map {
//            it.Functions.map {
//                it.Name
//            }
//        }

        val element = Instruction(
            instruction,
            output = output,
            input = """
                |Complete $language code, return rest code, no explaining
                |
                |```$language
                |$relatedCode
                |```
                |
                |Code:
                |```$language
                |$beforeCursorCode
                |```""".trimMargin()
        )

        return listOf(element)
    }
}

