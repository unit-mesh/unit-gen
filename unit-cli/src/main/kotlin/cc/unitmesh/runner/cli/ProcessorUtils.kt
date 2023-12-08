package cc.unitmesh.runner.cli

import com.charleskorn.kaml.Yaml
import org.slf4j.Logger
import java.io.File
import kotlin.system.exitProcess

object ProcessorUtils {
    private val logger: Logger = org.slf4j.LoggerFactory.getLogger(ProcessorUtils::class.java)

    fun loadConfig(): PreProcessorConfig {
        val file = File("processor.yml").let {
            if (!it.exists()) {
                logger.error("Config file not found: ${it.absolutePath}")
                exitProcess(1)
            }

            it
        }

        val content = file.readText()
        return Yaml.default.decodeFromString(deserializer = PreProcessorConfig.serializer(), content)
    }

}
