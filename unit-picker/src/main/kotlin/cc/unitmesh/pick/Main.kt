package cc.unitmesh.pick

import cc.unitmesh.pick.picker.CodePicker
import cc.unitmesh.pick.picker.PickerConfig
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option

class Picker : CliktCommand() {
    val url by option("-u", "--url", help = "url to pick code").default(".")

    override fun run() {
        val config = PickerConfig(url = url)
        CodePicker(config).execute()
    }
}

fun main(args: Array<String>) = Picker().main(args)
