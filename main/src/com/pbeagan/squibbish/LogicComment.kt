package com.pbeagan.squibbish

class LogicComment {
     fun logicComment(iterator: Iterator<String>) {
         while (iterator.hasNext()) {
             val next = iterator.next()
             if (next.isTerminator()) {
                 break
             }
         }
     }
}