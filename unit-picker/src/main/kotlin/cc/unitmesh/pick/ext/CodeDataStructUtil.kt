package cc.unitmesh.pick.ext

import chapi.domain.core.CodeContainer
import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodePosition

fun CodeContainer.buildSourceCode(codeLines: List<String>) {
    this.DataStructures.map { ds ->
        ds.Imports = this.Imports

        ds.Content = CodeDataStructUtil.contentByPosition(codeLines, ds.Position)
        ds.Functions.map {
            it.Content = CodeDataStructUtil.contentByPosition(codeLines, it.Position)
        }
    }
}

enum class NamingStyle(val value: String) {
    CAMEL_CASE("CamelCase"),
    SNAKE_CASE("snake_case"),
    KEBAB_CASE("kebab-case"),
    ;
}

fun CodeDataStruct.checkNamingStyle(): String {
    val countByNaming: MutableMap<NamingStyle, Int> = mutableMapOf(
        NamingStyle.CAMEL_CASE to 0,
        NamingStyle.SNAKE_CASE to 0,
        NamingStyle.KEBAB_CASE to 0,
    )
    this.Functions.map {
        // check by [CodeFunction.Name]
        val name = it.Name
        val nameStyle = when {
            name.contains("_") -> NamingStyle.SNAKE_CASE
            name.contains("-") -> NamingStyle.KEBAB_CASE
            else -> NamingStyle.CAMEL_CASE
        }

        countByNaming[nameStyle] = countByNaming[nameStyle]!! + 1
    }

    val maxCount = countByNaming.values.maxOrNull()!!
    val maxNamingStyle = countByNaming.filter { it.value == maxCount }.keys.first()

    return maxNamingStyle.value
}

fun CodeDataStruct.toSourceCode(): String {
    val result = StringBuilder()
    result.append("package ${this.Package};\n\n")
    this.Imports.forEach {
        result.append("import ${it.Source};\n")
    }

    result.append("\n")

    this.Annotations.forEach {
        result.append("@${it.Name}\n")
    }
    result.append(this.Content)

    return result.toString()
}


fun CodeDataStruct.toUml(): String {
    val output = StringBuilder()

    val superClass = Implements + if (Extend.isNotBlank()) {
        listOf(Extend)
    } else {
        emptyList()
    }
    val superClasses = if (superClass.isNotEmpty()) {
        ": " + superClass.joinToString(", ") + " "
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
                startLineContent.length
            }
        } else {
            position.StartLinePosition
        }

        val endColumn = if (position.StopLinePosition > endLineContent.length) {
            if (endLineContent.isBlank()) {
                0
            } else {
                endLineContent.length
            }
        } else {
            position.StopLinePosition
        }

        val start = startLineContent.substring(startColumn)
        val end = endLineContent.substring(endColumn)

        val code = if (startLine == endLine) {
            start
        } else {
            start + "\n" + lines.subList(startLine + 1, endLine).joinToString("\n") + "\n" + end
        }

        return code
    }
}