package cc.unitmesh.pick.prompt

import cc.unitmesh.pick.prompt.builder.AfterBlockCodeCompletionBuilder
import cc.unitmesh.pick.prompt.builder.InBlockCodeCompletionBuilder
import cc.unitmesh.pick.prompt.builder.InlineCodeCompletionBuilder
import cc.unitmesh.pick.prompt.builder.RelatedCodeCompletionBuilder
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
enum class InstructionType(val contentClass: KClass<out InstructionBuilder>) {
    INLINE_COMPLETION(InlineCodeCompletionBuilder::class),
    IN_BLOCK_COMPLETION(InBlockCodeCompletionBuilder::class),
    AFTER_BLOCK_COMPLETION(AfterBlockCodeCompletionBuilder::class),
    /**
     * the AutoDev with pre-build context
     */
    RELATED_CODE_COMPLETION(RelatedCodeCompletionBuilder::class);

    val type: String get() = name.lowercase()
    fun builder(context: InstructionContext): InstructionBuilder {
        return contentClass.constructors.first().call(context)
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