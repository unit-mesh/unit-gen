package cc.unitmesh.quality;

import cc.unitmesh.quality.extension.JavaServiceAnalyser
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class JavaServiceAnalyserTest {
    private fun loadNodes(source: String): List<CodeDataStruct> {
        return Json { ignoreUnknownKeys = true }.decodeFromString(
            File(this.javaClass.classLoader.getResource(source)!!.file).readText()
        )
    }
    @Test
    fun `should return empty list of issues when node is not a service`() {
        val nodes = loadNodes("java/structs_HelloController.json")
        val issues = JavaServiceAnalyser().analysis(nodes)

        assertEquals(0, issues.size)
    }
}
