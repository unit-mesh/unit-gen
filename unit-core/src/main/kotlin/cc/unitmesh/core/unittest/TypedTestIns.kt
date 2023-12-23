package cc.unitmesh.core.unittest

import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.completion.TypedIns

abstract class TypedTestIns : TypedIns {
    override val type: CompletionBuilderType = CompletionBuilderType.FULL_FILE_COMPLETION
    abstract val testType: TestCodeBuilderType
}
