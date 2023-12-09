package cc.unitmesh.eval

import cc.unitmesh.prompt.executor.ScriptExecutor
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import org.slf4j.Logger
import java.io.File

class EvalCommand : CliktCommand() {
    private val input by option(help = "prompt script config file").default("unit-eval.yml")

    override fun run() {
        // check is yaml file
        if (!input.endsWith(".yaml") && !input.endsWith(".yml")) {
            throw Exception("input file should be a yaml file: $input")
        }

        // check input file exits
        val file = File(input)
        if (!file.exists()) {
            throw Exception("input file not found: ${input}")
        }

        // execute script
        val executor = ScriptExecutor(file.absoluteFile)
        executor.execute()

        logger.debug("execute script success: $input")
    }

    companion object {
        val logger: Logger = org.slf4j.LoggerFactory.getLogger(EvalCommand::class.java)
    }
}

fun main(args: Array<String>) = EvalCommand().main(args)
