package cc.unitmesh.pick.prompt.completion

import cc.unitmesh.core.completion.CompletionBuilder
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.worker.job.JobContext
import kotlinx.serialization.SerializationException

fun completionBuilders(types: List<CompletionBuilderType>, context: JobContext) : List<CompletionBuilder> {
    return types.map { builder(it, context) }
}

fun builder(completionBuilderType: CompletionBuilderType, context: JobContext): CompletionBuilder {
    return mapOf(
        CompletionBuilderType.INLINE_COMPLETION to InlineCodeCompletionBuilder(context),
        CompletionBuilderType.IN_BLOCK_COMPLETION to InBlockCodeCompletionBuilder(context),
        CompletionBuilderType.AFTER_BLOCK_COMPLETION to AfterBlockCodeCompletionBuilder(context),
    )[completionBuilderType] ?: throw SerializationException("Unknown message type: $completionBuilderType")
}