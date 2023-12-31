package cc.unitmesh.quality

import cc.unitmesh.quality.badsmell.BadsmellAnalyser
import chapi.ast.javaast.JavaAnalyser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

class BadsmellAnalyserTest {
    private fun getAbsolutePath(path: String): String {
        val resource = this.javaClass.classLoader.getResource(path)
        return Paths.get(resource!!.toURI()).toFile().absolutePath
    }

    @Test
    internal fun shouldIdentifyJavaEmptyTest() {
        val path = getAbsolutePath("bs/LazyClass.java")
        val data = JavaAnalyser().analysis(File(path).readText(), "LazyClass.java").DataStructures
        val results = BadsmellAnalyser().analysis(data)

        assertEquals(1, results.size)
        assertEquals("LazyClass.java", results[0].fullName)
        assertEquals("SMELL_LAZY_ELEMENT", results[0].name)
    }

    @Test
    fun shouldIdentifyLongParameters() {
        val path = getAbsolutePath("bs/LongParameter.java")
        val data = JavaAnalyser().analysis(File(path).readText(), "LongParameter.java").DataStructures
        val results = BadsmellAnalyser().analysis(data)

        assertEquals(1, results.size)
        assertEquals("LongParameter.java", results[0].fullName)
        assertEquals("SMELL_LONG_PARAMETER_LIST", results[0].name)
    }

    @Test
    @Disabled
    fun shouldIdentifyMultipleIf() {
        val path = getAbsolutePath("bs/MultipleIf.java")
        val data = JavaAnalyser().analysis(File(path).readText(), "MultipleIf.java").DataStructures
        val results = BadsmellAnalyser().analysis(data)

        assertEquals(1, results.size)
        assertEquals("MultipleIf.java", results[0].fullName)
        assertEquals("SMELL_COMPLEX_CONDITION", results[0].name)
    }

    @Test
    fun shouldIdentifyLargeClass() {
        val path = getAbsolutePath("bs/LargeClass.java")
        val data = JavaAnalyser().analysis(File(path).readText(), "LargeClass.java").DataStructures
        val results = BadsmellAnalyser().analysis(data)

        assertEquals(1, results.size)
        assertEquals("LargeClass.java", results[0].fullName)
        assertEquals("SMELL_LARGE_CLASS", results[0].name)
    }

    @Test
    @Disabled
    fun shouldIdentifyGraphCall() {
        val resource = this.javaClass.classLoader.getResource("bs/graphcall")
        val path = Paths.get(resource!!.toURI())

        val data = path.toFile().walk().mapNotNull {
            if (it.isFile) {
                JavaAnalyser().analysis(it.readText(), it.name).DataStructures
            } else {
                null
            }
        }.flatten().toList()

        val results = BadsmellAnalyser().analysis(data)

        assertEquals(1, results.size)
        assertEquals("GraphCallA.java", results[0].fullName)
        assertEquals("", results[0].detail)
        assertEquals("SMELL_GARPH_CONNECTED_CALL", results[0].name)
    }
}