package com.hoffi.mpp.common.io.mpp

fun String.executeCommand(
    teeStdout: Boolean = true
): ProcessResult = MppProcess.executeCommand(this, teeStdout)

const val ECHOCMD_PREFIX = "-> "

data class ProcessResult(val returnCode: Int, val outputLines: List<String>)

interface IMppProcess {
    fun executeCommand(
        command: String,
        teeStdout: Boolean = true,
        echoCmdToErr: Boolean = false
    ): ProcessResult
    fun executeCommandFramed(
        command: String,
        teeStdout: Boolean = true,
        echoCmdToErr: Boolean = false
    ): ProcessResult
}

expect object MppProcess : IMppProcess {
    override fun executeCommand(
        command: String,
        teeStdout: Boolean,
        echoCmdToErr: Boolean
    ): ProcessResult
    override fun executeCommandFramed(
        command: String,
        teeStdout: Boolean,
        echoCmdToErr: Boolean
    ): ProcessResult
}
