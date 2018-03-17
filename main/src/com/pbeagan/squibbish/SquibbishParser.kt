package com.pbeagan.squibbish

import com.pbeagan.squibbish.SquibbishParser.BraceType.*
import java.util.*
import kotlin.collections.HashMap

class SquibbishParser {
    var braceStack: Stack<BraceType> = Stack()
    private var compiledString: String = ""
    val variables: HashMap<String, String> = HashMap()

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
                .replace(";;", "  ;;")
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
        val variableName = iterator.next()
        val separator = iterator.next()
        if (separator != "=") {
            error("Variable assignment done incorrectly")
        }
        val value = iterator.next()
        val terminator = iterator.next()
        if (terminator != ";") {
            error("Variable assignment done incorrectly")
        }

        if (!variables.containsKey(variableName)) {
            println("variable '$variableName' initialized with value '$value'")
        }
        variables[variableName] = value

        appendCompiled(" $variableName=")
        appendCompiled(value + ";".wrap())
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
        val functionName = iterator.getStringUntilTerminator("=", " ")
        var next = iterator.next()
        if (next != "{") {
            error("Function not followed by brace")
        }
        braceStack.push(FUNCTION)
        appendCompiled("$functionName () {".wrap())

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
                echoString += "$next "
            }
        }

        appendCompiled("echo $echoString;".wrap())
        if (exitChar == "}") {
            logicBraceEnd(iterator)
        }
    }

    private fun logicBranch(iterator: Iterator<String>) {
        val branchStatement = iterator.getStringUntilTerminator("{", "")
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
        val tokenIteratorFor = iterator.getStringUntilTerminator("{", " ")
        showPrint(tokenIteratorFor)
        val iterForMatcher = Regex(" *([^. ]*) *\\.\\. *([^. ]*) *(\\.\\.)? *([^. ]*) *([^. ]*)?").findAll(tokenIteratorFor)

        if (iterForMatcher.any()) {
            braceStack.push(FOR)
            val values = iterForMatcher.iterator().next().groupValues
            showPrint(values.toString())
            val start = values[1]
            val iter = values[4]
            val end = values[2]
            val alias = if (values[5].isNullOrEmpty()) "it" else values[5]
            appendCompiled("for $alias in `seq $start $iter $end`; do".wrap())
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

    private fun Iterator<String>.getStringUntilTerminator(terminator: String, seperator: String): String {
        var branchStatement = ""
        var next = ""
        while (next != terminator) {
            branchStatement += next + seperator
            next = this.next()
        }
        return branchStatement
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