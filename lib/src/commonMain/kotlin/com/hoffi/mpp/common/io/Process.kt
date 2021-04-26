package com.hoffi.mpp.common.io

fun String.executeCommand(
    redirectStderr: Boolean = true
): String? = MppProcess.executeCommand(this, redirectStderr)

interface IMppProcess {
    fun executeCommand(
        command: String,
        redirectStderr: Boolean = true
    ): String?
}

expect object MppProcess : IMppProcess {
    override fun executeCommand(
        command: String,
        redirectStderr: Boolean
    ): String?
}
