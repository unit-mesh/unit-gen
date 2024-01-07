package cc.unitmesh.pick.builder.strategy;

import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.strategy.bizcode.SimilarChunksStrategyBuilder
import cc.unitmesh.pick.worker.job.JobContext
import cc.unitmesh.quality.CodeQualityType
import chapi.ast.javaast.JavaAnalyser
import io.kotest.matchers.shouldBe
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Test
import java.io.File


class SimilarChunksStrategyBuilderTest {
    @Test
    fun shouldReturnEmptySimilarChunk() {
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
            insOutputConfig = InsOutputConfig(),
            instructionBuilderTypes = listOf(InstructionBuilderType.IN_BLOCK_COMPLETION),
            maxTypedCompletionSize = 3,
            insQualityThreshold = InsQualityThreshold()
        )
        val builder = SimilarChunksStrategyBuilder(context)
        val result = builder.build()

        result.size shouldBe 0
    }

    @Test
    fun shouldBeSimilarChunkByPackageName() {
        val model = File(this.javaClass.classLoader.getResource("related/BlogPost.java")!!.file).readText()
        val modelContainer = JavaAnalyser().analysis(model, "BlogPost.java")
        val repository = File(this.javaClass.classLoader.getResource("related/BlogRepository.java")!!.file).readText()
        val repositoryContainer = JavaAnalyser().analysis(repository, "BlogRepository.java")
        val service = File(this.javaClass.classLoader.getResource("related/BlogService.java")!!.file).readText()
        val serviceContainer = JavaAnalyser().analysis(service, "BlogService.java")

        val job = InstructionFileJob(
            FileJob(
            ),
            codeLines = service.lines(),
            code = service,
            container = serviceContainer
        )

        val context = JobContext(
            job = job,
            qualityTypes = listOf(CodeQualityType.JavaController),
            fileTree = hashMapOf(
                "cc.unitmesh.testng.entity.BlogPost" to InstructionFileJob(
                    FileJob(
                    ),
                    codeLines = model.lines(),
                    code = model,
                    container = modelContainer
                ),
                "cc.unitmesh.testng.repository.BlogRepository" to InstructionFileJob(
                    FileJob(
                    ),
                    codeLines = repository.lines(),
                    code = repository,
                    container = repositoryContainer
                ),
                "cc.unitmesh.testng.service.BlogService" to job
            ),
            insOutputConfig = InsOutputConfig(),
            instructionBuilderTypes = listOf(InstructionBuilderType.IN_BLOCK_COMPLETION),
            maxTypedCompletionSize = 3,
            insQualityThreshold = InsQualityThreshold()
        )

        val builder = SimilarChunksStrategyBuilder(context)
        val result = builder.build()

        result.size shouldBe 3

        result.first().unique().input shouldBe """
// Similar chunk:
```
// Compare this snippet from cc.unitmesh.testng.repository.BlogRepository
// 
// @Repository
// public interface BlogRepository extends CrudRepository<BlogPost, Long> {
// 
// }
//
```

Code:
```
package cc.unitmesh.testng.service;

import cc.unitmesh.testng.entity.BlogPost;
import cc.unitmesh.testng.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogService {
    @Autowired
    BlogRepository blogRepository;

    public BlogPost createBlog(BlogPost blogDto) {
```"""
    }
}
