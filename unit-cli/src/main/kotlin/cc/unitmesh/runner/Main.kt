package cc.unitmesh.runner

import cc.unitmesh.pick.picker.PickerConfig
import cc.unitmesh.pick.picker.SimpleCodePicker
import cc.unitmesh.runner.cli.ProcessorResult
import cc.unitmesh.runner.cli.ProcessorUtils
import cc.unitmesh.runner.cli.SourceCode
import com.github.ajalt.clikt.core.CliktCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

private val logger = org.slf4j.LoggerFactory.getLogger(UnitCommand::class.java)

class UnitCommand : CliktCommand() {

    override fun run() {
        val outputDir = File("datasets" + File.separator + "origin")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        logger.info("Runner started: ${outputDir.absolutePath}")


        val evalConfig = ProcessorUtils.loadConfig()
        val projects = evalConfig.projects

        runBlocking {
            val deferredResults = projects.map {
                async { processProject(it) }
            }

            deferredResults.forEach { deferred ->
                val result = deferred.await()
                val outputFile = File(outputDir, result.repository.split("/").last() + ".json")
                val json = Json { prettyPrint = true }
                outputFile.writeText(json.encodeToString(result.content))
            }
        }
    }
}

suspend fun processProject(code: SourceCode): ProcessorResult {
    return withContext(Dispatchers.Default) {
        logger.info("Start to process ${code.repository}")
        val pickerConfig = PickerConfig(
            url = code.repository,
            branch = code.branch,
            language = code.language
        )

        ProcessorResult(
            repository = code.repository,
            content = SimpleCodePicker(pickerConfig).execute()
        )
    }
}

fun main(args: Array<String>) = UnitCommand().main(args)
