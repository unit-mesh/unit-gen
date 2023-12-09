---
layout: default
title: Instruction
nav_order: 3
has_children: true
permalink: /instruction
---

# InstructionType

InstructionType is a enum class that defines the type of instruction. It is used to determine which builder to use to
build the instruction.

```kotlin
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
```
