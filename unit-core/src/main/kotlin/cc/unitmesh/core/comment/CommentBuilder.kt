package cc.unitmesh.core.comment

import cc.unitmesh.core.completion.TypedIns
import chapi.domain.core.CodeDataStruct

interface CommentBuilder {
    fun build(dataStruct: CodeDataStruct): List<TypedIns>
}


