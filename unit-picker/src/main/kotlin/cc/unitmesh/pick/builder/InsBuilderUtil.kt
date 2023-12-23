package cc.unitmesh.pick.builder

import cc.unitmesh.core.completion.CompletionBuilder
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.strategy.base.TestBuilder
import cc.unitmesh.pick.builder.bizcode.AfterBlockCodeCompletionBuilder
import cc.unitmesh.pick.builder.bizcode.InBlockCodeCompletionBuilder
import cc.unitmesh.pick.builder.bizcode.InlineCodeCompletionBuilder
import cc.unitmesh.pick.builder.unittest.ApiTestBuilder
import cc.unitmesh.pick.builder.unittest.ClassTestBuilder
import cc.unitmesh.pick.builder.unittest.MethodTestBuilder
import cc.unitmesh.pick.worker.job.JobContext
import kotlinx.serialization.SerializationException

fun completionBuilders(types: List<CompletionBuilderType>, context: JobContext) : List<CompletionBuilder> {
    return types.map { completionBuilder(it, context) }
}

fun completionBuilder(completionBuilderType: CompletionBuilderType, context: JobContext): CompletionBuilder {
    return mapOf(
        CompletionBuilderType.INLINE_COMPLETION to InlineCodeCompletionBuilder(context),
        CompletionBuilderType.IN_BLOCK_COMPLETION to InBlockCodeCompletionBuilder(context),
        CompletionBuilderType.AFTER_BLOCK_COMPLETION to AfterBlockCodeCompletionBuilder(context),
    )[completionBuilderType] ?: throw SerializationException("Unknown message type: $completionBuilderType")
}


fun testBuilders(types: List<TestCodeBuilderType>, context: JobContext) : List<TestBuilder> {
    return types.map { testBuilder(it, context) }
}

fun testBuilder(type: TestCodeBuilderType, context: JobContext): TestBuilder {
    return mapOf(
        TestCodeBuilderType.METHOD_UNIT to MethodTestBuilder(context),
        TestCodeBuilderType.CLASS_UNIT to ClassTestBuilder(context),
        TestCodeBuilderType.API_UNIT to ApiTestBuilder(context),
    )[type] ?: throw SerializationException("Unknown message type: $type")
}
