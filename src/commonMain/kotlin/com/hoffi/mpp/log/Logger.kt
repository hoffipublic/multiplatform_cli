package com.hoffi.mpp.log

import kotlin.reflect.KClass

interface MPPLogger {
    //val logLineFormat : String
    //    get() = "(%s): %s - %s "
    fun toLogLine(msg: String, timestamp: String, loggerClass: String, platform: String = "") : String {
        return "$platform($timestamp): ${loggerClass} - $msg"
    }
    fun info(msg: String)
    fun info(msg: String, t: Throwable)
}

expect class Logger(kClass: KClass<*>) : MPPLogger {
}
