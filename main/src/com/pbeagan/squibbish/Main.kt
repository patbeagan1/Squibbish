package com.pbeagan.squibbish

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Needs exactly one param.");
        exitProcess(1)
    }

    val input = args[0]

    SquibbishParser().parse(input)
}

