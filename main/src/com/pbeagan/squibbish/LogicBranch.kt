package com.pbeagan.squibbish

import java.util.*

class LogicBranch(private val printer: SQBPrinter, val logicBranchInner: LogicBranchInner, val logicCaseInner: LogicCaseInner) {
    fun logicBranch(iterator: Iterator<String>, braceStack: Stack<SquibbishParser.BraceType>) {
        val branchStatement = iterator.getStringUntilTerminator("{", "")
        val iterForMatcher = Regex(" *([^ ]*) *").findAll(branchStatement)
        if (iterForMatcher.any()) {
            val values = iterForMatcher.iterator().next().groupValues
            val switch = values[0]
            if (switch.isEmpty()) {
                braceStack.push(SquibbishParser.BraceType.BRANCH)
                logicBranchInner.logicBranchInner(iterator, previousToken = "", braceStack = braceStack)
            } else {
                braceStack.push(SquibbishParser.BraceType.CASE)
                printer.appendCompiled("case \"\${$switch}\" in".wrap())
                logicCaseInner.logicCaseInner(iterator, previousToken = "", braceStack = braceStack)
            }
        } else {
            throw SQBCompilationError("Branch started incorrectly.")
        }
    }
}