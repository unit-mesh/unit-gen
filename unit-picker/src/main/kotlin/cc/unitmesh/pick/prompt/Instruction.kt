package cc.unitmesh.pick.prompt

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Instruction(
    val instruction: String,
    val input: String,
    val output: String,
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
    }
}
