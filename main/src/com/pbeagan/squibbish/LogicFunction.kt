package com.pbeagan.squibbish

import java.util.*

class LogicFunction(private val printer: SQBPrinter) {
     fun logicFunction(iterator: Iterator<String>, braceStack: Stack<SquibbishParser.BraceType>) {
        val functionName = iterator.getStringUntilTerminator("=", " ")
        var next = iterator.next()
        if (next != "{") {
            throw SQBCompilationError("Function not followed by brace")
        }
        braceStack.push(SquibbishParser.BraceType.FUNCTION)
        printer.appendCompiled("$functionName () {".wrap())

        next = iterator.next()
        var counter = 1
        while (next != "|") {
            if (!Regex("[ a-zA-Z]*").matches(next)) {
                throw SQBCompilationError("Function param name does not match regex. Did you include the pipe? '|'")
            }
            next.replace(" ", "_")
            printer.appendCompiled("local $next=\"\${$counter}\";".wrap())
            next = iterator.next()
            counter++
        }
    }
}