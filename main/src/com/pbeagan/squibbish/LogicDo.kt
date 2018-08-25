package com.pbeagan.squibbish

class LogicDo(private val printer: SQUPrinter) {
    fun logicDo(iterator: Iterator<String>) {
        var next = iterator.next()
        var bashString = ""
        while (next.isTerminator().not()) {
            if (next == "{") {
                error("Braces are not allowed in the DO macro.")
            }
            bashString += next.wrap()
            next = iterator.next()
        }
        printer.appendCompiled(bashString.wrap() + ";".wrap())
    }
}