package com.hoffi.mpp.log

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.reflect.KClass

actual class Logger actual constructor(val kClass: KClass<*>) : MPPLogger {
    override fun info(msg: String) {
        println(toLogLine(msg, LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).toString(), kClass.java.name, "JVM"))
    }

    override fun info(msg: String, t: Throwable) {
        info(msg)
        t.printStackTrace()
    }
}
