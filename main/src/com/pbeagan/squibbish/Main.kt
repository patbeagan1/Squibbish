package com.pbeagan.squibbish

import java.io.File
import java.io.InputStream
import java.io.OutputStream
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
    val fileName = args[1]
    val squibbishParser = SquibbishParser()
    var writeToFile = false
    when (input) {
        "-f" -> {
            input = readFromFile(fileName, input)
        }
        "-fo" -> {
            writeToFile = true
            input = readFromFile(fileName, input)
        }
    }
    val parse = squibbishParser.parse(input)
    if (writeToFile) {
        writeToFile(parse)
    }
}

private fun readFromFile(fileName: String, input: String): String {
    var input1 = input
    val inputStream: InputStream = File(fileName).inputStream()
    input1 = inputStream.bufferedReader().use { it.readText() }
    return input1
}

private fun writeToFile(output: String) {
    val outputStream: OutputStream = File("out.sh").outputStream()
    outputStream.bufferedWriter().use { it.write(output) }
}

