package cc.unitmesh.runner.cli

import kotlinx.serialization.Serializable

@Serializable
data class UnitEvalConfig(
    val projects: List<SourceCode>
)

@Serializable
data class SourceCode(
    val repository: String,
    val branch: String,
    val language: String
)