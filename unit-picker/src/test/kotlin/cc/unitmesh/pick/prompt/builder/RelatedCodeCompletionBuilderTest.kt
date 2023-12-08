package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.picker.InstructionJob
import cc.unitmesh.pick.prompt.InstructionContext
import cc.unitmesh.quality.CodeQualityType
import chapi.ast.javaast.JavaAnalyser
import chapi.domain.core.CodeDataStruct
import kotlinx.serialization.json.Json
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class RelatedCodeCompletionBuilderTest {
    private fun loadNodes(source: String): List<CodeDataStruct> {
        return Json { ignoreUnknownKeys = true }.decodeFromString(
            File(this.javaClass.classLoader.getResource(source)!!.file).readText()
        )
    }

    @Test
    fun shouldReturnEmptyWhenHasQualityIssue() {
        val code = """package com.example.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/blog/get")
	public String index() {
		return "Greetings from Spring Boot!";
	}

}"""
        val container = JavaAnalyser().analysis(code, "HelloController.java")
        val job = InstructionJob(
            FileJob(
            ),
            codeLines = code.lines(),
            code = code,
            container = container
        )
        val context = InstructionContext(
            job = job,
            qualityTypes = listOf(CodeQualityType.JavaController),
            fileTree = hashMapOf("" to job)
        )
        val builder = RelatedCodeCompletionBuilder(context)
        val result = builder.convert()

        assertEquals(0, result.size)
    }

    @Test
    fun shouldReturnOneItemWhenNoQualityIssue() {
        val code = """package com.example.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

}"""
        val container = JavaAnalyser().analysis(code, "HelloController.java")
        val job = InstructionJob(
            FileJob(
            ),
            codeLines = code.lines(),
            code = code,
            container = container
        )
        val context = InstructionContext(
            job = job,
            qualityTypes = listOf(CodeQualityType.JavaController),
            fileTree = hashMapOf("" to job)
        )
        val builder = RelatedCodeCompletionBuilder(context)
        val result = builder.convert()

        assertEquals(1, result.size)
    }
}