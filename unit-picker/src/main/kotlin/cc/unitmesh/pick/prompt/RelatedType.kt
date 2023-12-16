package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.prompt.builder.*
import kotlinx.serialization.SerializationException

/**
 * InstructionType is a enum class to define all supported instruction type.
 *
 * TODO: split strategy for different patterns.
 *
 */
enum class RelatedType {
    /**
     * the AutoDev with pre-build context for un-support language
     */
    SIMILAR_CHUNKS_COMPLETION,

    /**
     * the AutoDev with pre-build context
     */
    RELATED_CODE_COMPLETION;

    fun builder(context: InstructionContext): InstructionBuilder<out Any> {
        return mapOf(
            SIMILAR_CHUNKS_COMPLETION to SimilarChunksCompletionBuilder(context),
            RELATED_CODE_COMPLETION to RelatedCodeCompletionBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}

enum class CompletionType {
    INLINE_COMPLETION,
    IN_BLOCK_COMPLETION,
    AFTER_BLOCK_COMPLETION;

    fun builder(context: InstructionContext) : InstructionBuilder<out Any> {
        return mapOf(
            INLINE_COMPLETION to InlineCodeCompletionBuilder(context),
            IN_BLOCK_COMPLETION to InBlockCodeCompletionBuilder(context),
            AFTER_BLOCK_COMPLETION to AfterBlockCodeCompletionBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}