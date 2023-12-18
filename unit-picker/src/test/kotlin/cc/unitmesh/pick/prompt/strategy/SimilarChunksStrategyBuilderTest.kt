package cc.unitmesh.pick.prompt.strategy;

import cc.unitmesh.pick.builder.BuilderConfig
import cc.unitmesh.pick.builder.InstructionFileJob
import cc.unitmesh.pick.prompt.JobContext
import cc.unitmesh.quality.CodeQualityType
import chapi.ast.javaast.JavaAnalyser
import io.kotest.matchers.shouldBe
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Test


class SimilarChunksStrategyBuilderTest {
    @Test
    fun shouldReturnEmptySimlarChunk() {
        val code = """package com.example.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/blog")
	public String index() {
		return "Greetings from Spring Boot!";
	}

}"""
        val container = JavaAnalyser().analysis(code, "HelloController.java")
        val job = InstructionFileJob(
            FileJob(
            ),
            codeLines = code.lines(),
            code = code,
            container = container
        )
        val context = JobContext(
            job = job,
            qualityTypes = listOf(CodeQualityType.JavaController),
            fileTree = hashMapOf("" to job),
            builderConfig = BuilderConfig()
        )
        val builder = SimilarChunksStrategyBuilder(context)
        val result = builder.build()

        val ins = result[0]
        ins.similarChunks.size shouldBe 0
        ins.afterCursor shouldBe "		return \"Greetings from Spring Boot!\";\n\t}"
    }
}
