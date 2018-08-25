package com.pbeagan.squibbish

import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size > 2 || args.isEmpty()) {
        println("""
This program takes either 1 or 2 arguments, you supplied (${args.size})

Usage:
    squibbish "echo hello"
    squibbish -f "myfilename.txt"
                """)
        exitProcess(1)
    }
    var input = args[0]
    val fileName = args[1]
    val squibbishParser = SquibbishParser()
    var writeToFile = false
    when (input) {
        "-f" -> {
            input = readFromFile(fileName)
        }
        "-fo" -> {
            writeToFile = true
            input = readFromFile(fileName)
        }
    }
    val parse = squibbishParser.parse(input)
    if (writeToFile) {
        writeToFile(parse)
    }
}

private fun readFromFile(fileName: String): String = File(fileName)
        .inputStream()
        .bufferedReader()
        .use { it.readText() }

private fun writeToFile(output: String) {
    File("out.sh")
            .outputStream()
            .bufferedWriter()
            .use { it.write(output) }
}

