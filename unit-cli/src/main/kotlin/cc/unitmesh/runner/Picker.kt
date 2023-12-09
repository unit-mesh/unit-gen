package cc.unitmesh.runner

import cc.unitmesh.pick.config.PickerOption
import cc.unitmesh.pick.SimpleCodePicker
import cc.unitmesh.pick.prompt.Instruction
import cc.unitmesh.runner.cli.ProcessorResult
import cc.unitmesh.runner.cli.ProcessorUtils
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.runBlocking
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
            projects.map { code ->
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
            }.forEach { result ->
                finalResult.addAll(result.content)

                val json = Json { prettyPrint = true }
                File(outputDir, result.outputName).writeText(json.encodeToString(result.content))
            }

            val outputFile = File(outputDir, "summary.json")
            val json = Json { prettyPrint = true }
            outputFile.writeText(json.encodeToString(finalResult))
        }
    }
}

// Since the network is not stable, we need to retry
//
//    private fun extracted(projects: List<SourceCode>, outputDir: File) {
//        runBlocking {
//            val deferredResults = projects.map {
//                async { processProject(it) }
//            }
//
//            deferredResults.forEach { deferred ->
//                val result = deferred.await()
//                val outputFile = File(outputDir, result.repository.split("/").last() + ".json")
//                val json = Json { prettyPrint = true }
//                outputFile.writeText(json.encodeToString(result.content))
//            }
//        }
//    }
//
//suspend fun processProject(code: SourceCode): ProcessorResult {
//    return withContext(Dispatchers.Default) {
//        logger.info("Start to process ${code.repository}")
//        val pickerConfig = PickerConfig(
//            url = code.repository,
//            branch = code.branch,
//            language = code.language
//        )
//
//        val content = SimpleCodePicker(pickerConfig).execute()
//        ProcessorResult(
//            repository = code.repository,
//            content = content
//        )
//    }
//}

