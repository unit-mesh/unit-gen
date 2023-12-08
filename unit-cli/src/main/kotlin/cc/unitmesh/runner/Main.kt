package cc.unitmesh.runner

import cc.unitmesh.pick.picker.PickerConfig
import cc.unitmesh.pick.picker.SimpleCodePicker
import cc.unitmesh.runner.cli.ProcessorResult
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.runBlocking
import cc.unitmesh.runner.cli.ProcessorUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class UnitCommand : CliktCommand() {
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    override fun run() {
        val outputDir = File("datasets" + File.separator + "origin")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        logger.info("Runner started: ${outputDir.absolutePath}")


        val evalConfig = ProcessorUtils.loadConfig()
        val projects = evalConfig.projects

        runBlocking {
            val jobs = projects.map {
                async(Dispatchers.IO) {
                    val pickerConfig = PickerConfig(
                        url = it.repository,
                        branch = it.branch,
                        language = it.language
                    )

                    ProcessorResult(
                        repository = it.repository,
                        content = SimpleCodePicker(pickerConfig).execute()
                    )
                }
            }

            jobs.awaitAll().forEach {
                val outputFile = File(outputDir, it.repository.split("/").last() + ".json")
                val json = Json { prettyPrint = true }
                outputFile.writeText(json.encodeToString(it.content))
            }
        }
    }
}

fun main(args: Array<String>) = UnitCommand().main(args)
