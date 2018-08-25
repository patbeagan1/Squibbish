package com.pbeagan.squibbish

import java.util.*

class SquibbishParser : SQUPrinter, LogicRoot {
    var braceStack: Stack<BraceType> = Stack()
    private var compiledString: String = ""

    fun parse(input: String): String {
        val iterator = input
                .replace("\n", NEWLINE.wrap())
                .replace(";", ";".wrap())
                .replace("{", "{".wrap())
                .replace("}", "}".wrap())
                .replace("(", "(".wrap())
                .replace(")", ")".wrap())
                .replace("[", "[".wrap())
                .replace("]", "]".wrap())
                .replace("=", "=".wrap())
                .replace("\\.\\.", "..".wrap())
                .split(Regex("(\\s)+"))
                .iterator()
        applyLogic(iterator)

        compiledString = compiledString
                .replace(Regex(" +"), " ")
                .replace(";;", "  ;;")
                .replace(" ;", ";")
                .replace("elif [ 1 ]; then :; fi;", "fi;")
        println(compiledString)
        return compiledString
    }

    private val logicDo = LogicDo(this)
    private val logicFunction = LogicFunction(this)
    private val logicComment = LogicComment(this)
    private val logicBranchInner = LogicBranchInner(this)
    private val logicCaseInner = LogicCaseInner(this)
    private val logicBraceEnd = LogicBraceEnd(this, logicBranchInner, logicCaseInner)
    private val logicBranch = LogicBranch(this, logicBranchInner, logicCaseInner)
    private val logicFor = LogicFor(this, this)
    private val logicEcho = LogicEcho(this, logicComment, logicBraceEnd)
    private val logicBash = LogicBash(this)
    private val logicMath = LogicMath(this)
    private val logicLet = LogicLet(this, logicMath)

    override fun applyLogic(iterator: Iterator<String>) {
        while (iterator.hasNext()) {
            val token = iterator.next()
            when (token) {
                "//" -> logicComment.logicComment(iterator)
                "br", "branch" -> logicBranch.logicBranch(iterator, braceStack)
                "for" -> logicFor.logicFor(iterator, braceStack)
                "echo" -> logicEcho.logicEcho(iterator, braceStack)
                "}" -> logicBraceEnd.logicBraceEnd(iterator, braceStack)
                "bash" -> logicBash.logicBash(iterator)
                "math" -> logicMath.performWith(iterator)
                "fn" -> logicFunction.logicFunction(iterator, braceStack)
                "do" -> logicDo.logicDo(iterator)
                "let" -> logicLet.logicLet(iterator)
            }
        }
    }

    override fun showPrint(toPrint: String) {
        println("\'$toPrint\'")
    }

    override fun appendCompiled(s: String) {
        println("#$s")
        compiledString += s
    }

    enum class BraceType {
        FOR, BRANCH, BRANCH_INNER, FUNCTION, CASE, CASE_INNER
    }

    companion object {
        const val NEWLINE = "NEWLINE"
    }
}
