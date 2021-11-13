package com.hoffi.mpp.common.io.mpp

import platform.posix.*

actual object Console : IConsole {
    override fun clearConsole() {
        system("clear")
    }

    override fun inputLine(prompt: String, hideInput: Boolean): String {
        // hideInput is not currently implemented
        if ((prompt != "") && isatty(STDIN_FILENO) != 0) print(prompt)
        return readLine() ?: throw Exception("readLine() returned null")
    }

    override fun echo(s: String) {
        fprintf(stdout, s + "\n")
    }

    override fun echon(s: String) {
        fprintf(stdout, s)
    }

    override fun echoErr(s: String) {
        fprintf(stderr, s + "\n")
        fflush(stderr)
    }

    override fun echoErrn(s: String) {
        fprintf(stderr, s)
        fflush(stderr)
    }
}
