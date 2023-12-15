package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.prompt.builder.*
import kotlinx.serialization.SerializationException

/**
 * InstructionType is a enum class to define all supported instruction type.
 *
 * TODO: split strategy for different patterns.
 *
 */
enum class InstructionType {
    // we can use a full-line model to complete.
    INLINE_COMPLETION,

    // TODO: maybe we don't need this
    IN_BLOCK_COMPLETION,

    // TODO: maybe we don't need this
    AFTER_BLOCK_COMPLETION,

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
            INLINE_COMPLETION to InlineCodeCompletionBuilder(context),
            IN_BLOCK_COMPLETION to InBlockCodeCompletionBuilder(context),
            AFTER_BLOCK_COMPLETION to AfterBlockCodeCompletionBuilder(context),
            SIMILAR_CHUNKS_COMPLETION to SimilarChunksCompletionBuilder(context),
            RELATED_CODE_COMPLETION to RelatedCodeCompletionBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}
