package cc.unitmesh.quality;

import cc.unitmesh.quality.extension.JavaControllerAnalyser
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class JavaControllerAnalyserTest {
    private fun loadNodes(source: String): List<CodeDataStruct> {
        return Json { ignoreUnknownKeys = true }.decodeFromString(
            File(this.javaClass.classLoader.getResource(source)!!.file).readText()
        )
    }
    @Test
    fun `should return list of issues when checking API`() {
        val nodes = loadNodes("java/structs_HelloController.json")
        val issues = JavaControllerAnalyser().analysis(nodes)

        assertEquals(2, issues.size)
    }
}
