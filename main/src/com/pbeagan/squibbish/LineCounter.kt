package com.pbeagan.squibbish

object LineCounter {
    var count = 0
    fun increment() = Unit.also { count += 1 }
}