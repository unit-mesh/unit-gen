package cc.unitmesh.pick.prompt

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
    IN_BLOCK_COMPLETION(InBlockCodeCompletion::class),
    AFTER_BLOCK_COMPLETION(AfterBlockCodeCompletion::class),
    /**
     * the AutoDev with pre-build context
     */
    RELATED_CODE_COMPLETION(RelatedCodeCompletion::class);

    val type: String get() = name.lowercase()
}

interface InstructionBuilder {
    fun build(): InstructionData
}

data class InstructionData(
    val instruction: String,
    val input: String,
    val output: String,
)

@Serializable
data class InlineCodeCompletionBuilder(
    val instruction: String,
    val output: String,
    val language: String,
    val beforeCursorCode: String,
) : InstructionBuilder {
    override fun build(): InstructionData {
        return InstructionData(
            instruction,
            output = output,
            input = """```$language
                |$beforeCursorCode
                |```""".trimMargin()
        )
    }
}

@Serializable
data class InBlockCodeCompletion(
    val instruction: String,
    val output: String,
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
) : InstructionBuilder {
    override fun build(): InstructionData {
        return InstructionData(
            instruction,
            output = output,
            input = """```$language
                |$beforeCursorCode
                |$afterCursorCode
                |```""".trimMargin()
        )
    }
}

class AfterBlockCodeCompletion(
    val instruction: String,
    val output: String,
    val language: String,
    val beforeCursorCode: String,
    val afterCursorCode: String,
) : InstructionBuilder {
    override fun build(): InstructionData {
        return InstructionData(
            instruction,
            output = output,
            input = """```$language
                |$beforeCursorCode
                |$afterCursorCode
                |```""".trimMargin()
        )
    }
}


@Serializable
data class RelatedCodeCompletion(
    val instruction: String,
    val output: String,
    val language: String,
    val beforeCursorCode: String,
    val relatedCode: String,
) : InstructionBuilder {
    override fun build(): InstructionData {
        return InstructionData(
            instruction,
            output = output,
            input = """
                | Compare this snippet:
                |```$language
                |$relatedCode
                |```
                |Code:
                |```$language
                |$beforeCursorCode
                |```""${'"'}.trimMargin()
            """.trimIndent()
        )
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


