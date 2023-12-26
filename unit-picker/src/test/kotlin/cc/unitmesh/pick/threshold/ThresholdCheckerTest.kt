package cc.unitmesh.pick.threshold

import cc.unitmesh.core.Instruction
import cc.unitmesh.pick.worker.WorkerContext
import cc.unitmesh.pick.worker.job.InstructionFileJob
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ThresholdCheckerTest {
    private val context = WorkerContext.default()

    @Test
    fun shouldReturnTrueWhenFileMeetsThresholdCriteria() {
        // given
        val job = InstructionFileJob(
            FileJob(
                extension = "java",
                complexity = 5,
                binary = false,
                generated = false,
                minified = false,
                bytes = 10000,
                content = "some code".toByteArray()
            )
        )

        // when
        val result = ThresholdChecker(context).isMetThreshold(job)

        // then
        assertTrue(result)
    }

    @Test
    fun shouldReturnFalseWhenFileDoesNotMeetExtensionCriteria() {
        // given
        val job = InstructionFileJob(
            FileJob (
                extension = "txt"
            )
        )

        // when
        val result = ThresholdChecker(context).isMetThreshold(job)

        // then
        assertFalse(result)
    }

    @Test
    fun shouldReturnFalseWhenFileDoesNotMeetComplexityCriteria() {
        // given
        val job = InstructionFileJob(
            FileJob(
                extension = "java",
                complexity = (InsQualityThreshold.MAX_COMPLEXITY + 1).toLong()
            )
        )

        // when
        val result = ThresholdChecker(context).isMetThreshold(job)

        // then
        assertFalse(result)
    }

    @Test
    fun shouldReturnFalseWhenFileIsBinary() {
        // given
        val job = InstructionFileJob(
            FileJob(
                extension = "java",
                binary = true
            )
        )

        // when
        val result = ThresholdChecker(context).isMetThreshold(job)

        // then
        assertFalse(result)
    }

    @Test
    fun shouldReturnFalseWhenFileIsGenerated() {
        // given
        val job = InstructionFileJob(
            FileJob(
                extension = "java",
                generated = true
            )
        )

        // when
        val result = ThresholdChecker(context).isMetThreshold(job)

        // then
        assertFalse(result)
    }

    @Test
    fun shouldReturnFalseWhenFileIsMinified() {
        // given
        val job = InstructionFileJob(
            FileJob(
                extension = "java",
                minified = true
            )
        )

        // when
        val result = ThresholdChecker(context).isMetThreshold(job)

        // then
        assertFalse(result)
    }

    @Test
    fun shouldReturnFalseWhenFileSizeExceedsThreshold() {
        // given
        val job = InstructionFileJob(
            FileJob(
                extension = "java",
                bytes = (InsQualityThreshold.MAX_FILE_SIZE + 1).toLong()
            )
        )

        // when
        val result = ThresholdChecker(context).isMetThreshold(job)

        // then
        assertFalse(result)
    }


    @Test
    fun shouldReturnTrueWhenInstructionMeetsThresholdCriteria() {
        // given
        val ins = Instruction(
            instruction = "some instruction",
            input = "some input",
            output = "some output"
        )

        // when
        val result = ThresholdChecker(context).isMetThreshold(ins)

        // then
        assertTrue(result)
    }
}
