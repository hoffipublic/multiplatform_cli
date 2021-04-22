package com.hoffi.web

import com.hoffi.mpp.log.logMessage

fun main(args: Array<String>) {
    App().doIt(args)
}

class App {
    val greeting = "should have a greeting"

    fun doIt(args: Array<String>) {
        if (args.isNotEmpty()) {
            logMessage("${App::class.simpleName!!} $greeting from ${args.joinToString()}")
        } else {
            logMessage("${App::class.simpleName!!} $greeting}")
        }
    }
}
