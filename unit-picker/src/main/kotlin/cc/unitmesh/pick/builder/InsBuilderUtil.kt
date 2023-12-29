package cc.unitmesh.pick.builder

import cc.unitmesh.core.completion.TypedInsBuilder
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.unittest.TestCodeBuilder
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.builder.bizcode.AfterBlockCodeTypedInsBuilder
import cc.unitmesh.pick.builder.bizcode.InBlockCodeTypedInsBuilder
import cc.unitmesh.pick.builder.bizcode.InlineCodeTypedInsBuilder
import cc.unitmesh.pick.builder.unittest.java.ClassTestCodeBuilder
import cc.unitmesh.pick.builder.unittest.java.JavaMethodTestCodeBuilder
import cc.unitmesh.pick.worker.job.JobContext
import kotlinx.serialization.SerializationException

fun completionBuilders(types: List<CompletionBuilderType>, context: JobContext) : List<TypedInsBuilder> {
    return types.map { completionBuilder(it, context) }
}

fun completionBuilder(completionBuilderType: CompletionBuilderType, context: JobContext): TypedInsBuilder {
    return mapOf(
        CompletionBuilderType.INLINE_COMPLETION to InlineCodeTypedInsBuilder(context),
        CompletionBuilderType.IN_BLOCK_COMPLETION to InBlockCodeTypedInsBuilder(context),
        CompletionBuilderType.AFTER_BLOCK_COMPLETION to AfterBlockCodeTypedInsBuilder(context),
        CompletionBuilderType.TEST_CODE_GEN to TestCodeTypedInsBuilder(context),
        CompletionBuilderType.DOCUMENTATION to DocumentationTypedInsBuilder(context),
    )[completionBuilderType] ?: throw SerializationException("Unknown message type: $completionBuilderType")
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
