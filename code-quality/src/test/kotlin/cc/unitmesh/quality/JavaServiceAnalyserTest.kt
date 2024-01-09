package cc.unitmesh.quality;

import cc.unitmesh.quality.extension.JavaServiceAnalyser
import chapi.ast.javaast.JavaAnalyser
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.json.Json
import org.archguard.rule.core.RuleType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
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

    @Test
    fun `should identify too many repository dependencies`() {
        val path = getAbsolutePath("java/ServiceWithSixRepositories.java")
        val data = JavaAnalyser().analysis(File(path).readText(), "ServiceWithSixRepositories.java").DataStructures
        val issues = JavaServiceAnalyser().analysis(data)

        Assertions.assertEquals(1, issues.size)
        Assertions.assertEquals("TooManyRepositoryDependencies", issues[0].name)
        Assertions.assertEquals("Service should not dependent more than 5 repositories.", issues[0].detail)
        Assertions.assertEquals(RuleType.SERVICE_SMELL, issues[0].ruleType)
    }

    private fun getAbsolutePath(path: String): String {
        val resource = this.javaClass.classLoader.getResource(path)
        return Paths.get(resource!!.toURI()).toFile().absolutePath
    }
}
