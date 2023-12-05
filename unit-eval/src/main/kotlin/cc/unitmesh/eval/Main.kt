package org.unimesh.eval

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option

class Hello : CliktCommand() {
    val name by option(help = "your name")
    override fun run() {
        echo("Hello, $name!")
    }
}

fun main(args: Array<String>) = Hello().main(args)