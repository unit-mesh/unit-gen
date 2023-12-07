package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.prompt.InstructionBuilder
import cc.unitmesh.pick.prompt.InstructionContext
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodePosition

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

fun CodeDataStruct.toUml(): String {
    val output = StringBuilder()

    output.append("class $NodeName {\n")
    Fields.forEach {
        output.append("   ${it.TypeValue}: ${it.TypeType}\n")
    }

    var getterSetter: List<String> = listOf()
    val methodsWithoutGetterSetter = Functions.filter { it.Name != NodeName }
        .filter { it.Name !in listOf("toString", "hashCode", "equals") }
        .filter {
            val isGetter = it.Name.startsWith("get") && it.Parameters.isEmpty()
            val isSetter = it.Name.startsWith("set") && it.Parameters.size == 1
            if (isGetter || isSetter) {
                getterSetter = listOf(it.Name)
                return@filter false
            }
            return@filter true
        }

    if (getterSetter.isNotEmpty()) {
        output.append("\n   'getter/setter: ${getterSetter.joinToString(", ")}\n")
    }

    val methodCodes = methodsWithoutGetterSetter
        .joinToString("\n") { method ->
            val params =
                method.Parameters.joinToString("") { parameter -> "${parameter.TypeValue}: ${parameter.TypeType}" }
            "   + ${method.Name}($params)" + if (method.ReturnType.isNotBlank()) ": ${method.ReturnType}" else ""
        }

    if (methodCodes.isNotBlank()) {
        output.append("\n")
        output.append(methodCodes)
    }

    output.append("\n")
    output.append(" }\n")

    // TODO: split output and add comments line
    return output.split("\n").joinToString("\n") {
        "// $it"
    }
}

