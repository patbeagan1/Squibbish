package com.pbeagan.squibbish

class SQUCompilationError(s: String) : Throwable(s) {
    override val message: String?
        get() = "${LineCounter.count}\n${super.message}"
}