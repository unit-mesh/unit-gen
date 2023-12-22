package cc.unitmesh.quality.testbadsmell

import kotlinx.serialization.Serializable

@Serializable
data class TbsResult(var results: List<TestBadSmell>) {

}