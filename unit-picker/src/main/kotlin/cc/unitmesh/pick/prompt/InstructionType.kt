package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.prompt.builder.AfterBlockCodeCompletionBuilder
import cc.unitmesh.pick.prompt.builder.InBlockCodeCompletionBuilder
import cc.unitmesh.pick.prompt.builder.InlineCodeCompletionBuilder
import cc.unitmesh.pick.prompt.builder.RelatedCodeCompletionBuilder
import kotlinx.serialization.SerializationException

enum class InstructionType {
    INLINE_COMPLETION,
    IN_BLOCK_COMPLETION,
    AFTER_BLOCK_COMPLETION,

    /**
     * the AutoDev with pre-build context
     */
    RELATED_CODE_COMPLETION;

    fun builder(context: InstructionContext): InstructionBuilder<out Any> {
        return mapOf(
            INLINE_COMPLETION to InlineCodeCompletionBuilder(context),
            IN_BLOCK_COMPLETION to InBlockCodeCompletionBuilder(context),
            AFTER_BLOCK_COMPLETION to AfterBlockCodeCompletionBuilder(context),
            RELATED_CODE_COMPLETION to RelatedCodeCompletionBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}
