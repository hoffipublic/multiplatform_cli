package com.hoffi.mppcli.lib.common.io.mpp

import java.io.IOException

actual object Console : IConsole {
    override fun clearConsole() {
        Runtime.getRuntime().exec("clear")
    }

    override fun inputLine(prompt: String, hideInput: Boolean): String {
        // hideInput is not currently implemented
        if (prompt != "") print(prompt)
        return readLine() ?: throw IOException("readLine() returned null")
    }

    override fun echo(s: String) {
        System.out.println(s)
    }

    override fun echon(s: String) {
        System.out.print(s)
    }

    override fun echoErr(s: String) {
        System.err.println(s)
    }

    override fun echoErrn(s: String) {
        System.err.print(s)
    }
}
