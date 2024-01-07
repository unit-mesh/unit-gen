package cc.unitmesh.pick.builder

import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.builder.bizcode.AfterBlockCodeTypedInsBuilder
import cc.unitmesh.pick.builder.bizcode.InBlockCodeTypedInsBuilder
import cc.unitmesh.pick.builder.bizcode.InlineCodeTypedInsBuilder
import cc.unitmesh.pick.builder.unittest.java.ClassTestCodeBuilder
import cc.unitmesh.pick.builder.unittest.java.JavaMethodTestCodeBuilder
import cc.unitmesh.pick.worker.job.JobContext
import kotlinx.serialization.SerializationException

fun completionBuilders(types: List<InstructionBuilderType>, context: JobContext) : List<TypedInsBuilder> {
    return types.map { completionBuilder(it, context) }
}

fun completionBuilder(instructionBuilderType: InstructionBuilderType, context: JobContext): TypedInsBuilder {
    return mapOf(
        InstructionBuilderType.INLINE_COMPLETION to InlineCodeTypedInsBuilder(context),
        InstructionBuilderType.IN_BLOCK_COMPLETION to InBlockCodeTypedInsBuilder(context),
        InstructionBuilderType.AFTER_BLOCK_COMPLETION to AfterBlockCodeTypedInsBuilder(context),
        InstructionBuilderType.TEST_CODE_GEN to TestCodeTypedInsBuilder(context),
        InstructionBuilderType.DOCUMENTATION to DocumentationTypedInsBuilder(context),
    )[instructionBuilderType] ?: throw SerializationException("Unknown message type: $instructionBuilderType")
}

fun testBuilders(types: List<TestCodeBuilderType>, context: JobContext) : List<TestCodeBuilder> {
    return types.map { testBuilder(it, context) }
}

fun testBuilder(type: TestCodeBuilderType, context: JobContext): TestCodeBuilder {
    return mapOf(
        TestCodeBuilderType.METHOD_UNIT to JavaMethodTestCodeBuilder(context),
        TestCodeBuilderType.CLASS_UNIT to ClassTestCodeBuilder(context),
    )[type] ?: throw SerializationException("Unknown message type: $type")
}
