package cc.unitmesh.quality

import cc.unitmesh.quality.testbadsmell.TestBadsmellAnalyser
import chapi.ast.javaast.JavaAnalyser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

class TestBadsmellAnalyserTest {
    private fun getAbsolutePath(path: String): String {
        val resource = this.javaClass.classLoader.getResource(path)
        return Paths.get(resource!!.toURI()).toFile().absolutePath
    }

    @Test
    internal fun shouldIdentifyJavaEmptyTest() {
        val path = getAbsolutePath("tbs/EmptyTest.java")
        val data = JavaAnalyser().analysis(File(path).readText(), "EmptyTest.java").DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results[0].fileName, "EmptyTest.java")
        assertEquals(results[0].line, 8)
        assertEquals(results[0].type, "EmptyTest")
    }

    @Test
    internal fun shouldIdentifyJavaIgnoreTest() {
        val path = getAbsolutePath("tbs/IgnoreTest.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results[0].line, 7)
        assertEquals(results[0].type, "IgnoreTest")
    }

    @Test
    internal fun shouldIdentifyJavaRedundantPrintTest() {
        val path = getAbsolutePath("tbs/RedundantPrintTest.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results[0].line, 9)
        assertEquals(results[0].type, "RedundantPrintTest")
    }

    @Test
    internal fun shouldIdentifyJavaSleepyTest() {
        val path = getAbsolutePath("tbs/SleepyTest.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results[0].line, 8)
        assertEquals(results[0].type, "SleepyTest")
    }

    @Test
    internal fun shouldIdentifyRedundantAssertionTest() {
        val path = getAbsolutePath("tbs/RedundantAssertionTest.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results[0].line, 14)
        assertEquals(results[0].type, "RedundantAssertionTest")
    }

    @Test
    internal fun shouldIdentifyUnknownTest() {
        val path = getAbsolutePath("tbs/UnknownTest.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results[0].line, 7)
        assertEquals(results[0].type, "EmptyTest")
        assertEquals(results[1].line, 7)
        assertEquals(results[1].type, "UnknownTest")
    }

    @Test
    internal fun shouldIdentifyDuplicateAssertTest() {
        val path = getAbsolutePath("tbs/DuplicateAssertTest.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results[0].line, 9)
        assertEquals(results[0].type, "DuplicateAssertTest")
    }

    @Test
    internal fun shouldReturnEmptyWhenIsCreator() {
        val path = getAbsolutePath("tbs/regression/CreatorNotUnknownTest.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results.size, 0)
    }

    @Test
    internal fun shouldReturnEmptyWhenCallAssertInClassTests() {
        val path = getAbsolutePath("tbs/regression/CallAssertInClassTests.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results.size, 0)
    }

    @Test
    internal fun shouldReturnEmptyWhenCall() {
        val path = getAbsolutePath("tbs/regression/EnvironmentSystemIntegrationTests.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results.size, 0)
    }

    @Test
    internal fun shouldReturnMultipleResults() {
        val path = getAbsolutePath("tbs/regression/I18NTest.java")
        val data = JavaAnalyser().analysis(File(path).readText(), path).DataStructures
        val results = TestBadsmellAnalyser(data).analysis()

        assertEquals(results.size, 4)
    }
}