package com.pbeagan.squibbish

class LogicBash(private val printer: SQUPrinter) {
    fun logicBash(iterator: Iterator<String>) {
        var next = iterator.next()
        if (next != "{") {
            throw SQUCompilationError("Bash not followed by brace")
        }
        next = iterator.next()
        var bashString = ""
        while (next != "}") {
            if (next == "{") {
                throw SQUCompilationError("Braces are not allowed in the bash macro.")
            }
            bashString += next.wrap()
            next = iterator.next()
        }
        printer.appendCompiled(bashString.wrap())
    }
}