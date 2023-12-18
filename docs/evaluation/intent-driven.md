---
layout: default
title: Intent Driven
parent: Evaluation
nav_order: 6
---

# Code Generation design

## Trigger by Comments

```kotlin
/**
 * IntentDrivenCompletionIns
 *
 * IntentDrivenStrategyBuilder was similar to [RelatedCodeStrategyBuilder] and [SimilarChunksStrategyBuilder]
 */
```

## Trigger by Path

when the path is `src/main/java/com/unitmesh/uniteval/IntentDrivenStrategyBuilder.kt` will send similar inheritance code
from `CodeStrategyBuilder`.

## Trigger by Class Name and same package paths

- `class IntentCodeStrategyBuilder` will trigger Test.
- `class IntentCodeStrategyBuilder(private val context: JobContext):` will be working.

# How we implement

