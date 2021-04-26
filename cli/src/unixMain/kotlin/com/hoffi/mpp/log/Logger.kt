package com.hoffi.mpp.log

import kotlinx.datetime.*
import kotlin.reflect.KClass

actual class Logger actual constructor(val kClass: KClass<*>) : MPPLogger {
    override fun info(msg: String) {
        println(toLogLine(msg, Clock.System.now().toLocalDateTime(TimeZone.UTC).toString(), kClass.qualifiedName ?: "unknown", "MAC"))
    }

    override fun info(msg: String, t: Throwable) {
        info(msg)
        t.printStackTrace()
    }
}
