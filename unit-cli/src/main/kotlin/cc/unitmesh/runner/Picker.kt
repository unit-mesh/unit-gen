package cc.unitmesh.runner

import cc.unitmesh.pick.builder.PickerOption
import cc.unitmesh.pick.SimpleCodePicker
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.runner.cli.ProcessorResult
import cc.unitmesh.runner.cli.ProcessorUtils
import cc.unitmesh.runner.cli.SourceCode
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private val logger = org.slf4j.LoggerFactory.getLogger(PickerCommand::class.java)

fun main(args: Array<String>) = PickerCommand().main(args)

class PickerCommand : CliktCommand() {
    override fun run() {
        val outputDir = File("datasets" + File.separator + "origin")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        logger.info("Runner started: ${outputDir.absolutePath}")

        val evalConfig = ProcessorUtils.loadConfig()
        val projects = evalConfig.projects

        runBlocking {
            val finalResult: MutableList<Instruction> = mutableListOf()
            val deferredResults = projects.map { code ->
                async {
                    logger.info("Start to process ${code.repository}")
                    val pickerOption = PickerOption(
                        url = code.repository,
                        branch = code.branch,
                        language = code.language
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

                val json = Json { prettyPrint = true }
                File(outputDir, result.outputName).writeText(json.encodeToString(result.content))
            }

            val outputFile = File(outputDir, "summary.json")
            val json = Json { prettyPrint = true }
            outputFile.writeText(json.encodeToString(finalResult))

            logger.info("Runner finished: ${outputDir.absolutePath}")
            logger.info("Total size: ${finalResult.size}")
        }
    }
}
