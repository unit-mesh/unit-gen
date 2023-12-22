package cc.unitmesh.pick.prompt.base

import chapi.domain.core.CodeDataStruct
import chapi.domain.core.CodeFunction

interface CompletionBuilder {
    fun build(dataStruct: CodeDataStruct): List<CodeCompletionIns> {
        return listOf()
    }

    fun build(function: CodeFunction): List<CodeCompletionIns>
}


