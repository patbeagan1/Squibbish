package com.pbeagan.squibbish

import java.util.*

class LogicEcho(
        private val printer: SQBPrinter,
        private val logicComment: LogicComment,
        private val logicBraceEnd: LogicBraceEnd
) {
    fun logicEcho(iterator: Iterator<String>, braceStack: Stack<SquibbishParser.BraceType>) {
        var echoString = ""
        var exitChar = ""
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.isTerminator() || next == "}") {
                exitChar = next
                break
            } else {
                if (next == "//") {
                    logicComment.logicComment(iterator)
                } else {
                    echoString += "$next "
                }
            }
        }

        printer.appendCompiled("echo $echoString;".wrap())
        if (exitChar == "}") {
            logicBraceEnd.logicBraceEnd(iterator, braceStack = braceStack)
        }
    }
}