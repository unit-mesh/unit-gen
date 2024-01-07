package cc.unitmesh.pick.builder.completion;

import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.InstructionBuilderType
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodePosition
import io.kotest.matchers.shouldBe
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Test

class InlineCodeTypedInsBuilderTest {
    @Test
    fun should_return_list_of_code_completion_ins() {
        // Given
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
        val jobContext = JobContext(
            job,
            emptyList(),
            hashMapOf("" to job),
            InsOutputConfig(),
            emptyList(),
            3,
            insQualityThreshold = InsQualityThreshold()
        )
        val builder = InlineCodeTypedInsBuilder(jobContext)

        // When
        val result = builder.build(codeFunction)

        // Then
        result.size shouldBe 1
        result shouldBe listOf(
            CodeCompletionIns(
                beforeCursor = "println(\"",
                afterCursor = "Hello, world!\")",
                instructionBuilderType = InstructionBuilderType.INLINE_COMPLETION
            )
        )
    }
}
