package com.hoffi.mpp.common.io

import java.util.concurrent.TimeUnit

actual object MppProcess : IMppProcess {
    actual override fun executeCommand(
        command: String,
        redirectStderr: Boolean
    ): String? {
        return runCatching {
            ProcessBuilder(command.split(Regex("(?<!(\"|').{0,255}) | (?!.*\\1.*)")))
                //.directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .apply { if (redirectStderr) this.redirectError(ProcessBuilder.Redirect.PIPE) }
                .start().apply { waitFor(60L, TimeUnit.SECONDS) }
                .inputStream.bufferedReader().readText()
        }.onFailure { it.printStackTrace() }.getOrNull()
    }
}
