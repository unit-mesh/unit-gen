package cc.unitmesh.quality

import kotlinx.serialization.Serializable

@Serializable
enum class CodeQualityType {
    BadSmell,
    TestBadSmell,
    JavaController,
    JavaRepository,
    JavaService,
}
