package com.hoffi.mpp.log

import kotlin.reflect.KClass

interface MPPLogger {
    fun info(msg: String)
    fun info(msg: String, t: Throwable)
}

expect class Logger(kClass: KClass<*>) : MPPLogger {
}
