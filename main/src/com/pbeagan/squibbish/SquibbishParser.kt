package com.pbeagan.squibbish

import com.pbeagan.squibbish.SquibbishParser.BraceType.*
import java.util.*

class SquibbishParser {
    var braceStack: Stack<BraceType> = Stack()
    private var compiledString: String = ""

    fun parse(input: String): String {
        val parseable = input
                .replace("\n", ";".wrap())
                .replace(";", ";".wrap())
                .replace("{", "{".wrap())
                .replace("}", "}".wrap())
                .replace("(", "(".wrap())
                .replace(")", ")".wrap())
                .replace("[", "[".wrap())
                .replace("]", "]".wrap())
                .replace("=", "=".wrap())
                .replace("\\.\\.", "..".wrap())
        val iterator = parseable.split(Regex("(\\s)+")).iterator()
        applyLogic(iterator)

        compiledString = compiledString
                .replace(Regex(" +"), " ")
                .replace(" ;", ";")
                .replace("elif [ 1 ]; then :; fi;", "fi;")
        println(compiledString)
        return compiledString
    }

    private fun applyLogic(iterator: Iterator<String>) {
        while (iterator.hasNext()) {
            val token = iterator.next()
            when (token) {
                "//" -> logicComment(iterator)
                "br", "branch" -> logicBranch(iterator)
                "for" -> logicFor(iterator)
                "echo" -> logicEcho(iterator)
                "}" -> logicBraceEnd(iterator)
                "bash" -> logicBash(iterator)
                "fn" -> logicFunction(iterator)
                "do" -> logicDo(iterator)
                "let" -> logicLet(iterator)
            }
        }
    }

    private fun logicLet(iterator: Iterator<String>) {
        var next = iterator.next()
        appendCompiled(" $next")
        next = iterator.next()
        if (next != "=") {
            error("Variable assignment done incorrectly")
        }
        appendCompiled("=")
        next = iterator.next()
        appendCompiled(next + ";".wrap())
        next = iterator.next()
        if (next != ";") {
            error("Variable assignment done incorrectly")
        }
    }

    private fun logicDo(iterator: Iterator<String>) {
        var next = iterator.next()
        var bashString = ""
        while (next != ";") {
            if (next == "{") {
                error("Braces are not allowed in the DO macro.")
            }
            bashString += next.wrap()
            next = iterator.next()
        }
        appendCompiled(bashString.wrap() + ";".wrap())
    }

    private fun logicFunction(iterator: Iterator<String>) {
        var functionName = ""
        var next = ""
        while (next != "=") {
            if (!Regex("[ a-zA-Z]*").matches(next)) {
                error("Function name does not match regex")
            }
            functionName += next
            next = iterator.next()
        }
        next = iterator.next()
        if (next != "{") {
            error("Function not followed by brace")
        }
        braceStack.push(FUNCTION)
        functionName = functionName.replace(" ", "_")
        appendCompiled("$functionName () {")

        next = iterator.next()
        var counter = 1
        while (next != "|") {
            if (!Regex("[ a-zA-Z]*").matches(next)) {
                error("Function param name does not match regex")
            }
            next.replace(" ", "_")
            appendCompiled("local $next=\"\${$counter}\";".wrap())
            next = iterator.next()
            counter++
        }
    }

    private fun logicBash(iterator: Iterator<String>) {
        var next = iterator.next()
        if (next != "{") {
            error("Bash not followed by brace")
        }
        next = iterator.next()
        var bashString = ""
        while (next != "}") {
            if (next == "{") {
                error("Braces are not allowed in the bash macro.")
            }
            bashString += next.wrap()
            next = iterator.next()
        }
        appendCompiled(bashString.wrap())
    }

    private fun logicEcho(iterator: Iterator<String>) {
        var echoString = ""
        var exitChar = ""
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next == ";" || next == "}") {
                exitChar = next
                break
            } else {
                echoString += next
            }
        }

        appendCompiled("echo $echoString;".wrap())
        if (exitChar == "}") {
            logicBraceEnd(iterator)
        }
    }

    private fun logicBranch(iterator: Iterator<String>) {
        var branchStatement = ""
        var next = ""
        while (next != "{") {
            branchStatement += next
            next = iterator.next()
        }
        val iterForMatcher = Regex(" *([^ ]*) *").findAll(branchStatement)
        if (iterForMatcher.any()) {
            val values = iterForMatcher.iterator().next().groupValues
            val switch = values[0]
            if (switch.isEmpty()) {
                braceStack.push(BRANCH)
                logicBranchInner(iterator, previousToken = "")
            } else {
                braceStack.push(CASE)
                appendCompiled("case \"\${$switch}\" in".wrap())
                logicCaseInner(iterator, previousToken = "")
            }
        } else {
            error("Branch started incorrectly.")
        }
    }

    private fun logicCaseInner(iterator: Iterator<String>, previousToken: String) {
        var condition = previousToken
        var next = iterator.next()
        while (next == ";") {
            next = iterator.next()
        }
        while (next != "{") {
            if (next != ";") {
                condition += next.wrap()
            }
            next = iterator.next()
        }
        braceStack.push(CASE_INNER)
        appendCompiled(condition.wrap() + ")".wrap())
    }

    private fun logicBranchInner(iterator: Iterator<String>, previousToken: String) {
        var condition = previousToken
        var next = iterator.next()
        while (next == ";") {
            next = iterator.next()
        }
        while (next != "{") {
            if (next != ";") {
                condition += next.wrap()
            }
            next = iterator.next()
        }
        braceStack.push(BRANCH_INNER)
        if (previousToken == "") {
            appendCompiled("if [ ".wrap())
        }
        appendCompiled(condition.wrap() + "];".wrap())
        appendCompiled("then".wrap())
    }

    private fun logicComment(iterator: Iterator<String>) {
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next == ";") {
                break
            }
        }
    }

    var pipeBlock = false
    private fun getAndChangePipeBlock(): Boolean {
        pipeBlock = !pipeBlock
        return pipeBlock
    }

    private fun logicBraceEnd(iterator: Iterator<String>) {
        val pop = braceStack.pop()
        when (pop) {
            FOR -> appendCompiled("done;".wrap())
            BRANCH -> appendCompiled(" 1 ]; then :; fi;".wrap())
            BRANCH_INNER -> {
                appendCompiled("elif [".wrap())
                var next = iterator.next()
                if (next == ";") {
                    next = iterator.next()
                }
                if (next == "}") {
                    logicBraceEnd(iterator)
                } else {
                    logicBranchInner(iterator, next)
                }
            }
            FUNCTION -> appendCompiled("};".wrap())
            CASE -> appendCompiled("esac;".wrap())
            CASE_INNER -> {
                appendCompiled(";;".wrap())
                var next = iterator.next()
                if (next == ";") {
                    next = iterator.next()
                }
                if (next == "}") {
                    logicBraceEnd(iterator)
                } else {
                    logicCaseInner(iterator, next)
                }
            }
            else -> {
                error("Brace ended without starting.")
            }
        }
    }

    private fun logicFor(iterator: Iterator<String>) {
        var tokenIteratorFor = ""
        var next = ""
        while (next != "{") {
            tokenIteratorFor += next
            next = iterator.next()
        }
        showPrint(tokenIteratorFor)
        val iterForMatcher = Regex(" *(.*) *\\.\\.(.*)(\\.\\.)? *(.*) *").findAll(tokenIteratorFor)

        if (iterForMatcher.any()) {
            braceStack.push(FOR)
            val values = iterForMatcher.iterator().next().groupValues
            val start = values[1]
            val iter = values[2]
            val end = values[4]

            appendCompiled("for it in `seq $start $iter $end`; do".wrap())
            applyLogic(iterator)
        } else {
            error("Compilation error on 'for'.")
        }
    }

    private fun showPrint(toPrint: String) {
        println("\'$toPrint\'")
    }

    private fun appendCompiled(s: String) {
        println("#$s")
        compiledString += s
    }

    private fun String.wrap(): String {
        return " $this "
    }

    enum class BraceType {
        FOR, BRANCH, BRANCH_INNER, FUNCTION, CASE, CASE_INNER
    }

    class SQUCompilationError(s: String) : Throwable(s)

    private fun error(s: String) {
        println("Compilation Error: $s")
        throw SQUCompilationError(s)
    }
}