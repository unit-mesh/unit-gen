package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.prompt.builder.*
import kotlinx.serialization.SerializationException

/**
 * The `CodeContextStrategy` enum class represents different strategies for generating code context in AutoDev.
 *
 * There are two available strategies:
 * 1. [CodeContextStrategy.SIMILAR_CHUNKS]: This strategy is used with pre-built context for unsupported languages.
 * It allows AutoDev to generate code context with similar code chunks completion.
 * 2. [CodeContextStrategy.RELATED_CODE]: This strategy is used with pre-built context. It allows AutoDev to
 * generate code context with related code completion.
 *
 * The strategies are used through the `builder` function, which takes an `InstructionContext` parameter and returns an `InstructionBuilder` object.
 *
 * Note that the `builder` function throws a `SerializationException` if the strategy is unknown.
 */
enum class CodeContextStrategy {
    /**
     * the AutoDev with pre-build context for un-support language
     */
    SIMILAR_CHUNKS,

    /**
     * the AutoDev with pre-build context
     */
    RELATED_CODE;

    fun builder(context: InstructionContext): CodeContextBuilder<out Any> {
        return mapOf(
            SIMILAR_CHUNKS to SimilarChunksCompletionBuilder(context),
            RELATED_CODE to RelatedCodeCompletionBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}

enum class CompletionType {
    // TODO: support in future for this
    INLINE_COMPLETION,
    IN_BLOCK_COMPLETION,

    // TODO: support in future for this
    AFTER_BLOCK_COMPLETION;

    fun builder(context: InstructionContext): CodeContextBuilder<out Any> {
        return mapOf(
            INLINE_COMPLETION to InlineCodeCompletionBuilder(context),
            IN_BLOCK_COMPLETION to InBlockCodeCompletionBuilder(context),
            AFTER_BLOCK_COMPLETION to AfterBlockCodeCompletionBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}