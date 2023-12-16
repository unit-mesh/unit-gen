package cc.unitmesh.pick.prompt

import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CodeCompletionIns(
    val language: String,
    val beforeCursor: String,
    val afterCursor: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}

interface InstructionBuilder {
    fun buildDataStruct(dataStruct: CodeDataStruct): List<CodeCompletionIns> {
        return listOf()
    }

    fun buildFun(function: CodeFunction): List<CodeCompletionIns>
}


