package cc.unitmesh.core.unittest

import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.core.completion.TypedIns

abstract class TypedTestIns : TypedIns {
    override val type: InstructionBuilderType = InstructionBuilderType.TEST_CODE_GEN
    abstract val testType: TestCodeBuilderType
}
