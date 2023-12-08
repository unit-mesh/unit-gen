package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.prompt.builder.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

@Serializable(InstructionTypeSerializer::class)
enum class InstructionType() {
    INLINE_COMPLETION(),
    IN_BLOCK_COMPLETION(),
    AFTER_BLOCK_COMPLETION(),
    /**
     * the AutoDev with pre-build context
     */
    RELATED_CODE_COMPLETION();

    val type: String get() = name.lowercase()
    fun builder(context: InstructionContext): InstructionBuilder<out Any> {
        return mapOf(
            INLINE_COMPLETION to InlineCodeCompletionBuilder(context),
            IN_BLOCK_COMPLETION to InBlockCodeCompletionBuilder(context),
            AFTER_BLOCK_COMPLETION to AfterBlockCodeCompletionBuilder(context),
            RELATED_CODE_COMPLETION to RelatedCodeCompletionBuilder(context),
        )[this] ?: throw SerializationException("Unknown message type: $this")
    }
}

object InstructionTypeSerializer : KSerializer<InstructionType> {
    private val cache: MutableMap<String, InstructionType> = hashMapOf()

    private fun getMessageType(type: String): InstructionType {
        return cache.computeIfAbsent(type) { newType ->
            InstructionType.values().firstOrNull { it.type == newType }
                ?: throw SerializationException("Unknown message type: $newType")
        }
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        InstructionType::class.qualifiedName!!, PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): InstructionType {
        return getMessageType(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: InstructionType) {
        encoder.encodeString(value.type)
    }
}