package com.pbeagan.squibbish

import com.pbeagan.squibbish.SquibbishParser.BraceType.*
import java.util.*

class LogicBraceEnd(
        private val printer: SQBPrinter,
        private val logicBranchInner: LogicBranchInner,
        private val logicCaseInner: LogicCaseInner
) {
    fun logicBraceEnd(iterator: Iterator<String>, braceStack: Stack<SquibbishParser.BraceType>) {
        printer.showPrint(braceStack.size.toString() + braceStack.peek().name)
        val pop = braceStack.pop()
        when (pop) {
            FOR -> printer.appendCompiled("done;".wrap())
            BRANCH -> printer.appendCompiled(" 1 ]; then :; fi;".wrap())
            BRANCH_INNER -> {
                printer.appendCompiled("elif [".wrap())
                var next = iterator.next()
                if (next.isTerminator()) {
                    next = iterator.next()
                }
                if (next == "}") {
                    logicBraceEnd(iterator, braceStack)
                } else {
                    logicBranchInner.logicBranchInner(iterator, next, braceStack)
                }
            }
            FUNCTION -> printer.appendCompiled("};".wrap())
            FUNCTION_SCOPED -> printer.appendCompiled(");".wrap())
            CASE -> printer.appendCompiled("esac;".wrap())
            CASE_INNER -> {
                printer.appendCompiled(";;".wrap())
                var next = iterator.next()
                if (next.isTerminator()) {
                    next = iterator.next()
                }
                if (next == "}") {
                    logicBraceEnd(iterator, braceStack)
                } else {
                    logicCaseInner.logicCaseInner(iterator, next, braceStack)
                }
            }
            else -> {
                throw SQBCompilationError("Brace ended without starting.")
            }
        }
    }
}