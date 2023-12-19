---
layout: default
title: Quality threshold
parent: Quality
nav_order: 1
permalink: /quality/threshold
---

Default threshold:

- File Size: 64kb, about 640 ~ 1280 lines, maybe some comments, like chinese
- Complexity: 100,


## BsThresholds

```kotlin
data class BsThresholds(
    val bsLongParasLength: Int = 5,
    val bsIfSwitchLength: Int = 8,
    val bsLargeLength: Int = 20,
    val bsMethodLength: Int = 30,
    val bsIfLinesLength: Int = 3,
)
```

## 