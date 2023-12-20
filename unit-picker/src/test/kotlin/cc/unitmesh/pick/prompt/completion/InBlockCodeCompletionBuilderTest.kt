package cc.unitmesh.pick.prompt.completion;

import cc.unitmesh.pick.builder.BuilderConfig
import cc.unitmesh.pick.builder.InstructionFileJob
import cc.unitmesh.pick.prompt.CodeCompletionIns
import cc.unitmesh.pick.prompt.JobContext
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodePosition
import io.kotest.matchers.shouldBe
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

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
        val jobContext = JobContext(job, emptyList(), hashMapOf("" to job), BuilderConfig(), emptyList())
        val builder = InBlockCodeCompletionBuilder(jobContext)

        // when
        val result = builder.build(codeFunction)

        // then
        result.size shouldBe 1
        result[0] shouldBe CodeCompletionIns(
            beforeCursor = codeLines.subList(0, 1).joinToString("\n"),
            afterCursor = codeLines.subList(1, 3).joinToString("\n")
        )
    }
}
