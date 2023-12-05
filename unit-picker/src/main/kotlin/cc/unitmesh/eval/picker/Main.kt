package cc.unitmesh.eval.picker

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option

class Picker : CliktCommand() {
    val url by option("-u", "--url", help = "url to pick code").default(".")

    override fun run() {
        val config = PickerConfig(url = url)
        val worker = CodePicker(config)
    }
}

fun main(args: Array<String>) = Picker().main(args)
