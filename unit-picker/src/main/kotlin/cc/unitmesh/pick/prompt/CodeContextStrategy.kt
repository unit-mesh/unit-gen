package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.prompt.strategy.RelatedCodeStrategyBuilder
import cc.unitmesh.pick.prompt.strategy.SimilarChunksStrategyBuilder
import cc.unitmesh.pick.prompt.strategy.TypedCompletion
import kotlinx.serialization.SerializationException

/**
 * The `CodeContextStrategy` enum class represents different strategies for generating code context in AutoDev.
 *
 * There are two available strategies:
 * 1. [CodeContextStrategy.SIMILAR_CHUNKS]: This prompt is used with pre-built context for unsupported languages.
 * It allows AutoDev to generate code context with similar code chunks builder.
 * 2. [CodeContextStrategy.RELATED_CODE]: This prompt is used with pre-built context. It allows AutoDev to
 * generate code context with similar code builder.
 *
 * The strategies are used through the `builder` function, which takes an `InstructionContext` parameter and returns an `InstructionBuilder` object.
 *
 * Note that the `builder` function throws a `SerializationException` if the prompt is unknown.
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

    fun builder(context: JobContext): CodeStrategyBuilder<out TypedCompletion> {
        return mapOf(
            SIMILAR_CHUNKS to SimilarChunksStrategyBuilder(context),
            RELATED_CODE to RelatedCodeStrategyBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}

