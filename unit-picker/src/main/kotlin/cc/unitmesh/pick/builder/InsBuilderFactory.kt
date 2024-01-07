package cc.unitmesh.pick.builder

import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.pick.builder.comment.DocumentationTypedInsBuilder
import cc.unitmesh.pick.builder.completion.AfterBlockCodeTypedInsBuilder
import cc.unitmesh.pick.builder.completion.InBlockCodeTypedInsBuilder
import cc.unitmesh.pick.builder.completion.InlineCodeTypedInsBuilder
import cc.unitmesh.pick.builder.unittest.TestCodeTypedInsBuilder
import cc.unitmesh.pick.worker.job.JobContext
import kotlinx.serialization.SerializationException

fun instructionBuilders(types: List<InstructionBuilderType>, context: JobContext) : List<TypedInsBuilder> {
    return types.map { instructionBuilder(it, context) }
}

fun instructionBuilder(instructionBuilderType: InstructionBuilderType, context: JobContext): TypedInsBuilder {
    return mapOf(
        InstructionBuilderType.INLINE_COMPLETION to InlineCodeTypedInsBuilder(context),
        InstructionBuilderType.IN_BLOCK_COMPLETION to InBlockCodeTypedInsBuilder(context),
        InstructionBuilderType.AFTER_BLOCK_COMPLETION to AfterBlockCodeTypedInsBuilder(context),
        InstructionBuilderType.TEST_CODE_GEN to TestCodeTypedInsBuilder(context),
        InstructionBuilderType.DOCUMENTATION to DocumentationTypedInsBuilder(context),
    )[instructionBuilderType] ?: throw SerializationException("Unknown message type: $instructionBuilderType")
}
