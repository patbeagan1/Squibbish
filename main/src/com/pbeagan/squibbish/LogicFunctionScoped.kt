package com.pbeagan.squibbish

import java.util.*

class LogicFunctionScoped(private val printer: SQUPrinter) {
    fun logicFunctionScoped(iterator: Iterator<String>, braceStack: Stack<SquibbishParser.BraceType>) {
        val functionName = iterator.getStringUntilTerminator("=", " ")
        var next = iterator.next()
        if (next != "{") {
            throw SQUCompilationError("Function not followed by brace")
        }
        braceStack.push(SquibbishParser.BraceType.FUNCTION_SCOPED)
        printer.appendCompiled("$functionName () (".wrap())

        next = iterator.next()
        var counter = 1
        while (next != "|") {
            if (!Regex("[ a-zA-Z]*").matches(next)) {
                error("Function param name does not match regex")
            }
            next.replace(" ", "_")
            printer.appendCompiled("local $next=\"\${$counter}\";".wrap())
            next = iterator.next()
            counter++
        }
    }
}
