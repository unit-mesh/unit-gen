package cc.unitmesh.quality.testbadsmell

import kotlinx.serialization.Serializable

@Serializable
data class TestBadSmell(
    var FileName: String = "",
    var Type: String = "",
    var Description: String = "",
    var Line: Int = 0
)