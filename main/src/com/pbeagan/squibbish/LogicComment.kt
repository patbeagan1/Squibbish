package com.pbeagan.squibbish

class LogicComment(private val printer: SQUPrinter) {
     fun logicComment(iterator: Iterator<String>) {
         while (iterator.hasNext()) {
             val next = iterator.next()
             if (next == ";") {
                 break
             }
         }
     }
}