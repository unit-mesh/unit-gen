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

class AfterBlockCodeCompletionBuilderTest {

    @Test
    fun should_success_split_after_block_code() {
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
        val jobContext = JobContext(job, emptyList(), hashMapOf("" to job), BuilderConfig(), emptyList(), 3)
        val builder = AfterBlockCodeCompletionBuilder(jobContext)

        val result = builder.build(codeFunction)

        result shouldBe listOf(
            CodeCompletionIns(
                beforeCursor = codeLines.subList(0, 3).joinToString("\n"),
                afterCursor = "// other code here"
            )
        )
    }
}
