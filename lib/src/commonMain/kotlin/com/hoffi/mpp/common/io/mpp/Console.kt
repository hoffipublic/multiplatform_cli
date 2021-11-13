package com.hoffi.mpp.common.io.mpp

interface IConsole {
    fun clearConsole()
    fun inputLine(prompt: String = "", hideInput: Boolean = false): String // hideInput is not currently implemented
    fun echo(s: String)
    fun echon(s: String)
    fun echoErr(s: String)
    fun echoErrn(s: String)
}

expect object Console : IConsole {
}
