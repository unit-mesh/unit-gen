package cc.unitmesh.pick

import cc.unitmesh.pick.picker.SimpleCodePicker
import cc.unitmesh.pick.picker.PickerConfig
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.runBlocking

class Picker : CliktCommand() {
    val url by option("-u", "--url", help = "url to pick code").default(".")

    override fun run() {
        val config = PickerConfig(url = url)

        runBlocking {
            SimpleCodePicker(config).execute()
        }
    }
}

fun main(args: Array<String>) = Picker().main(args)
