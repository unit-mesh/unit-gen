package cc.unitmesh.core.unittest

import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.completion.TypedIns

abstract class TypedTestIns : TypedIns {
    override val type: CompletionBuilderType = CompletionBuilderType.TEST_CODE_GEN
    abstract val testType: TestCodeBuilderType
}
