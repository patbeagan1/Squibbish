package com.pbeagan.squibbish

import java.util.*

class LogicFor(private val printer: SQUPrinter, private val logicRoot: LogicRoot) {
    fun logicFor(iterator: Iterator<String>, braceStack: Stack<SquibbishParser.BraceType>) {
        val tokenIteratorFor = iterator.getStringUntilTerminator("{", " ")
        printer.showPrint(tokenIteratorFor)
        val iterForMatcher = Regex(" *([^. ]*) *\\.\\. *([^. ]*) *(\\.\\.)? *([^. ]*) *([^. ]*)?").findAll(tokenIteratorFor)

        if (iterForMatcher.any()) {
            braceStack.push(SquibbishParser.BraceType.FOR)
            val values = iterForMatcher.iterator().next().groupValues
            printer.showPrint(values.toString())
            val start = values[1]
            val iter = values[4]
            val end = values[2]
            val alias = if (values[5].isEmpty()) "it" else values[5]
            printer.appendCompiled("for $alias in `seq $start $iter $end`; do".wrap())
            logicRoot.applyLogic(iterator)
        } else {
            error("Compilation error on 'for'.")
        }
    }
}