package cc.unitmesh.pick.prompt.completion

import cc.unitmesh.pick.prompt.completion.base.CompletionBuilder
import cc.unitmesh.pick.worker.JobContext
import kotlinx.serialization.SerializationException

enum class CompletionBuilderType {
    // TODO: support in future for this
    INLINE_COMPLETION,
    IN_BLOCK_COMPLETION,
    AFTER_BLOCK_COMPLETION;

    fun builder(context: JobContext): CompletionBuilder {
        return mapOf(
            INLINE_COMPLETION to InlineCodeCompletionBuilder(context),
            IN_BLOCK_COMPLETION to InBlockCodeCompletionBuilder(context),
            AFTER_BLOCK_COMPLETION to AfterBlockCodeCompletionBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}

fun completionBuilders(types: List<CompletionBuilderType>, context: JobContext) : List<CompletionBuilder> {
    return types.map { it.builder(context) }
}