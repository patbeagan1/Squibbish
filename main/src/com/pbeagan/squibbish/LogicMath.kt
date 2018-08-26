package com.pbeagan.squibbish

class LogicMath(private val printer: SQBPrinter) {
    fun performWith(iterator: Iterator<String>): String {
        var next = iterator.next()
        if (next != "{") {
            throw  SQBCompilationError("Bash not followed by brace")
        }
        next = iterator.next()
        var bashString = "$( echo \""
        while (next != "}") {
            if (next == "{") {
                throw  SQBCompilationError("Braces are not allowed in the bash macro.")
            }
            bashString += next.wrap()
            next = iterator.next()
        }
        bashString += "\" | bc )"
        return bashString
    }

    fun performWithAndPrint(iterator: Iterator<String>) {
        printer.appendCompiled(performWith(iterator).wrap())
    }
}