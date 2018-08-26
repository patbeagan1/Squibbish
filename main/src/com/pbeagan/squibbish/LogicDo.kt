package com.pbeagan.squibbish

import java.util.*

class LogicDo(private val printer: SQUPrinter, private val logicBraceEnd: LogicBraceEnd) {
    fun logicDo(iterator: Iterator<String>, braceStack: Stack<SquibbishParser.BraceType>) {
        var next: String? = iterator.next()
        var bashString = ""
        var exitedWithBraceEnd = false
        while (next != null && next.isTerminator().not()) {
            if (next == "{") {
                throw SQUCompilationError("Braces are not allowed in the DO macro.")
            }
            if (next == "}") {
                exitedWithBraceEnd = true
                break
            }
            bashString += next.wrap()
            next = iterator.nextOrNull()
        }
        printer.appendCompiled(bashString.wrap() + ";".wrap())
        if (exitedWithBraceEnd) {
            logicBraceEnd.logicBraceEnd(iterator, braceStack = braceStack)
        }
    }
}