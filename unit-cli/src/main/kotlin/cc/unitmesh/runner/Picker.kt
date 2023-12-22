package cc.unitmesh.runner

import cc.unitmesh.pick.SimpleCodePicker
import cc.unitmesh.pick.builder.MAX_COMPLETION_EACH_FILE
import cc.unitmesh.pick.builder.PickerOption
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.pick.threshold.QualityThreshold
import cc.unitmesh.runner.cli.ProcessorResult
import cc.unitmesh.runner.cli.ProcessorUtils
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.*
import java.io.File

private val logger = org.slf4j.LoggerFactory.getLogger(PickerCommand::class.java)

fun main(args: Array<String>) = PickerCommand().main(args)

class PickerCommand : CliktCommand() {
    private val gitDepth by option(help = "Git depth").int().default(1)

    private val projectTypedCompletionSize by option(help = "Limit each CompletionType size").int()
        .default(QualityThreshold.MAX_COMPLEXITY)
    private val maxCompletionEachFile by option(help = "Max completion / file").int().default(
        MAX_COMPLETION_EACH_FILE
    )
    private val maxCharInCode by option(help = "Max char in code").int().default(QualityThreshold.MAX_CHAR_IN_CODE)
    private val maxLineInCode by option(help = "Max line in code").int().default(QualityThreshold.MAX_LINE_IN_CODE)

    override fun run() {
        val outputDir = File("datasets" + File.separator + "origin")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        logger.info("Runner started: ${outputDir.absolutePath}")

        val evalConfig = ProcessorUtils.loadConfig()
        val projects = evalConfig.projects
        val instructionConfig = evalConfig.instructionConfig

        runBlocking {
            val finalResult: MutableList<Instruction> = mutableListOf()
            val deferredResults = projects.map { code ->
                async {
                    logger.info("Start to process ${code.repository}")
                    val pickerOption = PickerOption(
                        url = code.repository,
                        branch = code.branch,
                        language = code.language,
                        maxCompletionEachFile = maxCompletionEachFile,
                        gitDepth = gitDepth,
                        completionTypeSize = projectTypedCompletionSize,
                        maxCharInCode = maxCharInCode,
                        maxLineInCode = maxLineInCode,
                    )

                    val content = SimpleCodePicker(pickerOption).execute()
                    ProcessorResult(
                        repository = code.repository,
                        content = content,
                        outputName = pickerOption.repoFileName()
                    )
                }
            }

            val results = deferredResults.awaitAll()
            results.forEach { result ->
                finalResult.addAll(result.content)

                File(outputDir, result.outputName).writeText(result.content.joinToString("\n") {
                    it.render(mergeInput = instructionConfig.mergeInput)
                })
            }

            val outputFile = File(outputDir, "summary.jsonl")
            if (outputFile.exists()) {
                outputFile.delete()
            }

            finalResult.shuffle()
            finalResult.forEach {
                outputFile.appendText(it.render(mergeInput = instructionConfig.mergeInput) + "\n")
            }

            logger.info("Runner finished: ${outputDir.absolutePath}")
            logger.info("Total size: ${finalResult.size}")
        }
    }
}
