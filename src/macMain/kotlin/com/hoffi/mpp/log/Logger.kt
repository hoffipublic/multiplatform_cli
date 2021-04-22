package com.hoffi.mpp.log

import kotlin.reflect.KClass

actual class Logger actual constructor(val kClass: KClass<*>) : MPPLogger {
    override fun info(msg: String) {
        println("Mac: [${kClass.toString()}] $msg")
    }

    override fun info(msg: String, t: Throwable) {
        println("Mac: [${kClass.toString()}] ${msg}")
        t.printStackTrace()
    }
}
