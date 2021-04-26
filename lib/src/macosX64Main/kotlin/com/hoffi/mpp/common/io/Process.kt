package com.hoffi.mpp.common.io

import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

actual object MppProcess : IMppProcess {
    actual override fun executeCommand(
        command: String,
        redirectStderr: Boolean
    ): String? {
        val commandToExecute = if (redirectStderr) "$command 2>&1" else command
        val fp = popen(commandToExecute, "r") ?: error("Failed to run command: $command")

        val stdout = buildString {
            val buffer = ByteArray(4096)
            while (true) {
                val input = fgets(buffer.refTo(0), buffer.size, fp) ?: break
                append(input.toKString())
            }
        }

        val status = pclose(fp)
        if (status != 0) {
            error("Command `$command` failed with status $status${if (redirectStderr) ": $stdout" else ""}")
        }

        return stdout
    }
}
