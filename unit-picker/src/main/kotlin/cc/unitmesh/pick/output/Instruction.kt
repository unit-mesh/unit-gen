package cc.unitmesh.pick.output

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
enum class InstructionType(val contentClass: KClass<out Instruction>) {
    INLINE_COMPLETION(InlineCodeCompletion::class),
    IN_BLOCK_COMPLETION(InBlockCodeCompletion::class),
    AFTER_BLOCK_COMPLETION(AfterBlockCodeCompletion::class),
    /**
     * the AutoDev with pre-build context
     */
    RELATED_CODE_COMPLETION(RelatedCodeCompletion::class)
    ;

    val type: String get() = name.lowercase()
}

sealed interface Instruction {
    val output: String
    val instruction: String
    fun input(): String
}

@Serializable
data class InlineCodeCompletion(
    override val instruction: String,
    override val output: String,
    val language: String,
    val beforeCursorCode: String,
) : Instruction {
    override fun input(): String {
        return """```$language
            |$beforeCursorCode
            |```""".trimMargin()
    }
}

@Serializable
data class InBlockCodeCompletion(
    override val instruction: String,
    override val output: String,
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
) : Instruction {
    override fun input(): String {
        return """```$language
            |$beforeCursorCode
            |$afterCursorCode
            |```""".trimMargin()
    }
}

class AfterBlockCodeCompletion(
    override val instruction: String,
    override val output: String,
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
) : Instruction {
    override fun input(): String {
        return """```$language
            |$beforeCursorCode
            |$afterCursorCode
            |```""".trimMargin()
    }
}

@Serializable
data class RelatedCodeCompletion(
    override val instruction: String,
    override val output: String,
    val language: String,
    val beforeCursorCode: String,
    val relatedCode: String,
) : Instruction {
    override fun input(): String {
        return """
            | Compare this snippet:
            |```$language
            |$relatedCode
            |```
            |Code:
            |```$language
            |$beforeCursorCode
            |```""".trimMargin()
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


