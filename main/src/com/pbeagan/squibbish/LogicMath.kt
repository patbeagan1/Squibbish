package com.pbeagan.squibbish

class LogicMath(private val printer: SQUPrinter) {
    fun performWith(iterator: Iterator<String>): String {
        var next = iterator.next()
        if (next != "{") {
            error("Bash not followed by brace")
        }
        next = iterator.next()
        var bashString = "$(("
        while (next != "}") {
            if (next == "{") {
                throw  SQUCompilationError("Braces are not allowed in the bash macro.")
            }

            bashString += next.wrap()
            next = iterator.next()
        }
        bashString += "))"
        return bashString
    }

    fun performWithAndPrint(iterator: Iterator<String>) {
        printer.appendCompiled(performWith(iterator).wrap())
    }
}