package cc.unitmesh.pick.builder.bizcode;

import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodePosition
import io.kotest.matchers.shouldBe
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Test

class InBlockCodeCompletionBuilderTest {

    @Test
    fun should_return_single_CodeCompletionIns() {
        val codeFunction = CodeFunction(
            Position = CodePosition(1, 0, 3)
        )
        val codeLines = """
            fun myFunction() {
                println("Hello, world!")
            }
            // other code here
        """.trimIndent().lines()
        val job = InstructionFileJob(
            fileSummary = FileJob(),
            codeLines = codeLines,
            code = codeLines.joinToString("\n")
        )
        val jobContext = JobContext(job, emptyList(), hashMapOf("" to job), InsOutputConfig(), emptyList(), 3)
        val builder = InBlockCodeCompletionBuilder(jobContext)

        // when
        val result = builder.build(codeFunction)

        // then
        result.size shouldBe 1
        result[0] shouldBe CodeCompletionIns(
            beforeCursor = codeLines.subList(0, 1).joinToString("\n"),
            afterCursor = codeLines.subList(1, 3).joinToString("\n"),
            completionBuilderType = CompletionBuilderType.IN_BLOCK_COMPLETION
        )
    }
}
