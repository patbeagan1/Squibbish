package com.pbeagan.squibbish

class LogicLet(
        val printer: SQUPrinter,
        private val logicMath: LogicMath
) {
    private val variables: HashMap<String, String> = HashMap()

    fun logicLet(iterator: Iterator<String>) {
        val variableName = iterator.next()
        val separator = iterator.next()
        if (separator != "=") {
            throw SQUCompilationError("Variable assignment done incorrectly")
        }
        var value = iterator.next()
        if (value == "math") {
            value = logicMath.performWith(iterator)
        } else {
            val terminator = iterator.next()
            if (!terminator.isTerminator()) {
                throw SQUCompilationError("Variable assignment done incorrectly: $terminator")
            }
        }

        if (!variables.containsKey(variableName)) {
            println("$TAG # variable '$variableName' initialized with value = '$value'")
        } else {
            println("$TAG # variable '$variableName' REASSIGNED with value = '$value'")
        }
        variables[variableName] = value

        printer.appendCompiled(" $variableName=")
        printer.appendCompiled(value + ";".wrap())
    }

    companion object {
        val TAG: String = LogicLet::class.java.simpleName
    }
}