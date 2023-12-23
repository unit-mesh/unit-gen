package cc.unitmesh.core.unittest

import cc.unitmesh.core.Instruction

interface TypedTestIns {
    val type: TestCodeBuilderType

    fun unique(): Instruction
}
