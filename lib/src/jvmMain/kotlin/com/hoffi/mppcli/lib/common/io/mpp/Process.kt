package com.hoffi.mppcli.lib.common.io.mpp

import com.hoffi.mppcli.lib.common.io.output.OutputFrame
import com.hoffi.mppcli.lib.common.io.output.OutputFrame.LINES
import com.hoffi.mppcli.lib.common.io.output.OutputFrame.WHERE.FIRST
import com.hoffi.mppcli.lib.common.io.output.OutputFrame.WHERE.MIDDLE
import java.lang.Integer.max
import java.util.concurrent.TimeUnit

actual object MppProcess : IMppProcess {
    actual override fun executeCommand(
        command: String,
        teeStdout: Boolean,
        echoCmdToErr: Boolean
    ): ProcessResult {
        if (echoCmdToErr) Console.echoErr("$ECHOCMD_PREFIX$command")
        val outputLines = mutableListOf<String>()
        runCatching {
            val process = ProcessBuilder("/bin/bash", "-c", command)
                //.directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .start()
            val bufferedReader = process.inputStream.bufferedReader()
            var line= bufferedReader.readLine()
            while (line != null) {
                if (teeStdout) {
                    Console.echo(line)
                }
                outputLines.add(line)
                line = bufferedReader.readLine()
            }
            process.apply { waitFor(5L, TimeUnit.SECONDS) }
            return ProcessResult(process.exitValue(), outputLines)
        }.onFailure { it.printStackTrace() ; return ProcessResult(126, outputLines) }
        return ProcessResult(126, outputLines)
    }

    actual override fun executeCommandFramed(
        command: String,
        teeStdout: Boolean,
        echoCmdToErr: Boolean
    ): ProcessResult {
        var where = FIRST
        var max = 0
        if (echoCmdToErr) {
            val lines: LINES = OutputFrame.cmdHeader(command)
            max = lines.max
            Console.echoErr(lines.s)
            where = MIDDLE
        }
        val outputLines = mutableListOf<String>()
        runCatching {
            val process = ProcessBuilder("/bin/bash", "-c", command)
                //.directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .start()
            val bufferedReader = process.inputStream.bufferedReader()
            var line= bufferedReader.readLine()
            while (line != null) {
                if (teeStdout) {
                    max = max(line.length, max)
                    val lines: LINES = OutputFrame.cmdOutput(line, hasHeader = echoCmdToErr, where = where)
                    Console.echo(lines.s)
                    where = MIDDLE
                }
                outputLines.add(line)
                line = bufferedReader.readLine()
            }
            if (teeStdout) Console.echo(OutputFrame.cmdEnd(max))
            process.apply { waitFor(5L, TimeUnit.SECONDS) }
            return ProcessResult(process.exitValue(), outputLines)
        }.onFailure {
            it.printStackTrace()
            if (teeStdout) Console.echo(OutputFrame.cmdEnd(max))
            return ProcessResult(126, outputLines)
        }
        return ProcessResult(126, outputLines)
    }
}
