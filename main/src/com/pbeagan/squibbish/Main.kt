package com.pbeagan.squibbish

import java.io.File
import java.io.InputStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size > 2) {
        println("""
Usage:
    squibbish "echo hello"
    squibbish -f "myfilename.txt"
                """)
        exitProcess(1)
    }
    var input = args[0]

    if (input == "-f") {
        val inputStream: InputStream = File(args[1]).inputStream()
        input = inputStream.bufferedReader().use { it.readText() }
    }
    SquibbishParser().parse(input)
}

