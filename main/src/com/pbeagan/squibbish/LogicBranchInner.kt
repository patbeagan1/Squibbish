package com.pbeagan.squibbish

import java.util.*

class LogicBranchInner(private val printer: SQUPrinter) {
    fun logicBranchInner(iterator: Iterator<String>, previousToken: String, braceStack: Stack<SquibbishParser.BraceType>) {
        var condition = previousToken
        var next = iterator.next()
        while (next.isTerminator()) {
            next = iterator.next()
        }
        while (next != "{") {
            if (next != ";") {
                condition += next.wrap()
            }
            next = iterator.next()
        }
        braceStack.push(SquibbishParser.BraceType.BRANCH_INNER)
        if (previousToken == "") {
            printer.appendCompiled("if [ ".wrap())
        }
        printer.appendCompiled(condition.wrap() + "];".wrap())
        printer.appendCompiled("then".wrap())
    }
}