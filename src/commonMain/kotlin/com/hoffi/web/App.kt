package com.hoffi.web

import com.hoffi.mpp.log.Logger

fun main(args: Array<String>) {
    App().doIt(args)
}

class App {
    val log = Logger(this::class)
    val greeting = "should have a greeting"

    fun doIt(args: Array<String>) {
        if (args.isNotEmpty()) {
            log.info("$greeting from ${args.joinToString()}")
        } else {
            log.info("$greeting}")
        }
    }
}
