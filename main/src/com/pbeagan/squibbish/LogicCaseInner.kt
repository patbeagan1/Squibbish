package com.pbeagan.squibbish

import java.util.*

class LogicCaseInner(private val printer: SQUPrinter) {
    fun logicCaseInner(iterator: Iterator<String>, previousToken: String, braceStack: Stack<SquibbishParser.BraceType>) {
        var condition = previousToken
        var next = iterator.next()
        while (next.isTerminator()) {
            next = iterator.next()
        }
        while (next != "{") {
            if (next.isTerminator().not()) {
                condition += next.wrap()
            }
            next = iterator.next()
        }
        braceStack.push(SquibbishParser.BraceType.CASE_INNER)
        printer.appendCompiled(condition.wrap() + ")".wrap())
    }
}