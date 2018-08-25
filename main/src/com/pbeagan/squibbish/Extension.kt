package com.pbeagan.squibbish

fun Iterator<String>.nextOrNull(): String? = if (this.hasNext()) this.next() else null

fun String.wrap(): String = " $this "

fun String.isTerminator() = this == ";" || (this == SquibbishParser.NEWLINE).also { LineCounter.increment() }

fun Iterator<String>.getStringUntilTerminator(terminator: String, seperator: String): String {
    var branchStatement = ""
    var next = ""
    while (next != terminator) {
        branchStatement += next + seperator
        next = this.next()
    }
    return branchStatement
}

object LineCounter {
    var count = 0
    fun increment() = Unit.also { count += 1 }
}
//
//    class Conditional(var shouldContinue: Boolean)
//
//    inline fun Conditional.orElse(action: () -> Unit) {
//        if (this.shouldContinue) action()
//    }
//
//    inline fun Conditional.orElseIfTrue(condition: Boolean, action: () -> Unit): Conditional {
//        if (condition) {
//            action()
//        }
//        return this
//    }
//
//    inline fun Boolean.ifTrue(action: () -> Unit): Conditional {
//        if (this) action()
//        return Conditional(!this)
//    }