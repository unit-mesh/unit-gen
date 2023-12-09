package cc.unitmesh.pick.prompt.builder

import cc.unitmesh.pick.config.BuilderConfig
import cc.unitmesh.pick.config.SingleFileInstructionJob
import cc.unitmesh.pick.prompt.InstructionContext
import cc.unitmesh.quality.CodeQualityType
import chapi.ast.javaast.JavaAnalyser
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class RelatedCodeCompletionBuilderTest {

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
        val job = SingleFileInstructionJob(
            FileJob(
            ),
            codeLines = code.lines(),
            code = code,
            container = container
        )
        val context = InstructionContext(
            job = job,
            qualityTypes = listOf(CodeQualityType.JavaController),
            fileTree = hashMapOf("" to job),
            builderConfig = BuilderConfig()
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
        val job = SingleFileInstructionJob(
            FileJob(
            ),
            codeLines = code.lines(),
            code = code,
            container = container
        )
        val context = InstructionContext(
            job = job,
            qualityTypes = listOf(CodeQualityType.JavaController),
            fileTree = hashMapOf("" to job),
            builderConfig = BuilderConfig()
        )
        val builder = RelatedCodeCompletionBuilder(context)
        val result = builder.convert()

        assertEquals(1, result.size)
    }

    @Test
    fun shouldReturnEmptyWhenNoImports() {
        val model = File(this.javaClass.classLoader.getResource("related/BlogPost.java")!!.file).readText()
        val modelContainer = JavaAnalyser().analysis(model, "BlogPost.java")
        val repository = File(this.javaClass.classLoader.getResource("related/BlogRepository.java")!!.file).readText()
        val repositoryContainer = JavaAnalyser().analysis(repository, "BlogRepository.java")
        val service = File(this.javaClass.classLoader.getResource("related/BlogService.java")!!.file).readText()
        val serviceContainer = JavaAnalyser().analysis(service, "BlogService.java")

        val job = SingleFileInstructionJob(
            FileJob(
            ),
            codeLines = service.lines(),
            code = service,
            container = serviceContainer
        )

        val context = InstructionContext(
            job = job,
            qualityTypes = listOf(CodeQualityType.JavaController),
            fileTree = hashMapOf(
                "cc.unitmesh.testng.entity.BlogPost" to SingleFileInstructionJob(
                    FileJob(
                    ),
                    codeLines = model.lines(),
                    code = model,
                    container = modelContainer
                ),
                "cc.unitmesh.testng.repository.BlogRepository" to SingleFileInstructionJob(
                    FileJob(
                    ),
                    codeLines = repository.lines(),
                    code = repository,
                    container = repositoryContainer
                ),
                "cc.unitmesh.testng.service.BlogService" to job
            ),
            builderConfig = BuilderConfig()
        )

        val builder = RelatedCodeCompletionBuilder(context)
        val result = builder.convert()

        assertEquals(result.size, 4)
        val first = result.first()

        assertEquals(
            first.relatedCode, """// class BlogPost {
//    : Long
//    : String
//    : String
//    : String
// 
//    'getter/setter: setAuthor
// 
//  }
// 
// class BlogRepository {
// 
//  }
// """
        )
    }
}