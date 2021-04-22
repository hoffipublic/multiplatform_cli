package com.hoffi.mpp.log

import kotlin.reflect.KClass

actual class Logger actual constructor(val kClass: KClass<*>) : MPPLogger {
    override fun info(msg: String) {
        println("JVM: [${kClass.java.name}] $msg")
    }

    override fun info(msg: String, t: Throwable) {
        println("JVM: [${kClass.java.name}] ${msg}")
        t.printStackTrace()
    }
}
