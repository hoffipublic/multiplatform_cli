package com.hoffi.mpp.common.io.mpp

import com.hoffi.mpp.common.io.OutputFrame
import com.hoffi.mpp.common.io.OutputFrame.LINES
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen
import kotlin.math.max
import com.hoffi.mpp.common.io.OutputFrame.WHERE.FIRST as FIRST1

actual object MppProcess : IMppProcess {
    actual override fun executeCommand(
        command: String,
        teeStdout: Boolean,
        echoCmdToErr: Boolean
    ): ProcessResult {
        val outputLines = mutableListOf<String>()

        if (echoCmdToErr) Console.echoErr("$ECHOCMD_PREFIX$command")
        val fp = popen(command, "r") ?: error("Failed to run command: $command")

        val buffer = ByteArray(4096)
        while (true) {
            val input = fgets(buffer.refTo(0), buffer.size, fp) ?: break
            var inputLine = input.toKString()
            inputLine = if (inputLine.endsWith("\n")) inputLine.dropLast(1) else inputLine
            inputLine = if (inputLine.endsWith("\r")) inputLine.dropLast(1) else inputLine
            if (teeStdout) Console.echo(inputLine)
            outputLines.add(inputLine)
        }

        val status = pclose(fp)
        if (status != 0) {
            error("Command `$command` failed with status $status")
        }

        return ProcessResult(status, outputLines)
    }
    actual override fun executeCommandFramed(
        command: String,
        teeStdout: Boolean,
        echoCmdToErr: Boolean
    ): ProcessResult {
        var where = FIRST1
        var max = 0

        if (echoCmdToErr) {
            val lines: LINES = OutputFrame.cmdHeader(command)
            max = lines.max
            Console.echoErr(lines.s)
            where = OutputFrame.WHERE.MIDDLE
        }
        val outputLines = mutableListOf<String>()
        val fp = popen(command, "r") ?: error("Failed to run command: $command")
        val buffer = ByteArray(4096)
        while (true) {
            val input = fgets(buffer.refTo(0), buffer.size, fp) ?: break
            var line = input.toKString()
            line = if (line.endsWith("\n")) line.dropLast(1) else line
            line = if (line.endsWith("\r")) line.dropLast(1) else line
            if (teeStdout) {
                max = max(line.length, max)
                val lines: LINES = OutputFrame.cmdOutput(line, hasHeader = echoCmdToErr, where = where)
                Console.echo(lines.s)
                where = OutputFrame.WHERE.MIDDLE
            }
            outputLines.add(line)
        }

        val status = pclose(fp)
        if (status != 0) {
            error("Command `$command` failed with status $status")
        }

        if (teeStdout) Console.echo(OutputFrame.cmdEnd(max))

        return ProcessResult(status, outputLines)
    }
}
