package com.pbeagan.squibbish

class SQBCompilationError(s: String) : Throwable(s) {
    override val message: String?
        get() = "${LineCounter.count}\n${super.message}"
}