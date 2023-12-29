package cc.unitmesh.core.comments

import cc.unitmesh.core.completion.TypedIns
import chapi.domain.core.CodeDataStruct

interface CommentsBuilder {
    fun build(dataStruct: CodeDataStruct): List<TypedIns>
}


