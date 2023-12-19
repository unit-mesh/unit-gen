package cc.unitmesh.pick.ext

import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodePosition

fun CodeDataStruct.toUml(): String {
    val output = StringBuilder()

    val superClass = Implements + Extend
    val superClasses = if (superClass.isNotEmpty()) {
        ":" + superClass.joinToString(", ") + " "
    } else {
        ""
    }

    output.append("class $NodeName $superClasses{\n")
    Fields.forEach {
        output.append("   ${it.TypeKey}: ${it.TypeType}\n")
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
                method.Parameters.joinToString(", ") { parameter ->
                    "${parameter.TypeValue}: ${parameter.TypeType}"
                }
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

object CodeDataStructUtil {
    fun contentByPosition(lines: List<String>, position: CodePosition): String {
        val startLine = if (position.StartLine == 0) {
            0
        } else {
            position.StartLine - 1
        }
        val endLine = if (position.StopLine == 0) {
            0
        } else {
            position.StopLine - 1
        }

        val startLineContent = lines[startLine]
        val endLineContent = lines[endLine]

        val startColumn = if (position.StartLinePosition > startLineContent.length) {
            if (startLineContent.isBlank()) {
                0
            } else {
                startLineContent.length - 1
            }
        } else {
            position.StartLinePosition
        }

        val endColumn = if (position.StopLinePosition > endLineContent.length) {
            if (endLineContent.isBlank()) {
                0
            } else {
                endLineContent.length - 1
            }
        } else {
            position.StopLinePosition
        }

        val start = startLineContent.substring(startColumn)
        val end = endLineContent.substring(0, endColumn)

        // start + ... + end
        return if (startLine == endLine) {
            start
        } else {
            start + lines.subList(startLine + 1, endLine).joinToString("") + end
        }
    }
}