package cc.unitmesh.pick.builder.bizcode;

import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.core.completion.CodeCompletionIns
import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.JobContext
import chapi.domain.core.CodeFunction
import chapi.domain.core.CodePosition
import io.kotest.matchers.shouldBe
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Test

class AfterBlockCodeTypedInsBuilderTest {

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
        val jobContext = JobContext(
            job,
            emptyList(),
            hashMapOf("" to job),
            InsOutputConfig(),
            emptyList(),
            3,
            insQualityThreshold = InsQualityThreshold()
        )
        val builder = AfterBlockCodeTypedInsBuilder(jobContext)

        val result = builder.build(codeFunction)

        result shouldBe listOf(
            CodeCompletionIns(
                beforeCursor = codeLines.subList(0, 3).joinToString("\n"),
                afterCursor = "// other code here",
                completionBuilderType = CompletionBuilderType.AFTER_BLOCK_COMPLETION
            )
        )
    }
}
