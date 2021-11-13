package com.hoffi.mpp.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import com.hoffi.mpp.common.io.mpp.Console
import com.hoffi.mpp.common.io.mpp.MppProcess
import com.hoffi.mpp.common.io.mpp.ProcessResult
import com.hoffi.mpp.common.io.mpp.executeCommand
import mu.KotlinLogging

fun main(args: Array<String>) {
    App().main(args)
}

class App : CliktCommand() {
    private val log = KotlinLogging.logger {}
    val count: Int by option(help="Number of greetings").int().default(3)
    val name: String by option(help="The person to greet").prompt("Your name")

    override fun run() {
        echo("echo '--count ${count} times '--name=${name}':")
        (1..count).forEach { i ->
            echo("${i}: Hello $name!")
        }

        val userinput = Console.inputLine("Console input: ")
        Console.echoErr("stderr: input was: $userinput")

        MppProcess.executeCommandFramed("""
            echo "this xxx the first line" | sed 's/xxx/is/'
            echo "some fancy second line" 'with both quotes' \
                 over multiple lines
        """.trimIndent(), echoCmdToErr = true, teeStdout = true)

//        val longoutputcmd = "cat '/Users/hoffi/Documents/CalibreLibrary/metadata_db_prefs_backup.json'"
//        log.info { "executing: $longoutputcmd"}
//        val longResult: ProcessResult = MppProcess.executeCommandFramed(longoutputcmd, echoCmdToErr = true)
//        log.warn { "result code: ${longResult.returnCode}" }
//        log.warn { echo() ; longResult.outputLines.joinToString("\n") }
//        echo()

        val cmd = """
            ls -lsh \
               -Fp | awk "match(\${'$'}0, /^(.*)(${'$'}(whoami))(.*)${'$'}/, m) { print m[1] \"redacted\" m[3]; next };1"
            echo "multiple cmds possible"
        """.trimIndent()
        log.info { "\n'${cmd}' result START" }
        var result: ProcessResult = MppProcess.executeCommandFramed(cmd, echoCmdToErr = true)
        log.warn { "result code: ${result.returnCode}" }
        log.info { "result END" }
        result = MppProcess.executeCommandFramed(cmd, echoCmdToErr = false)
        result = MppProcess.executeCommand(cmd, echoCmdToErr = true)

        // jvm will erroneously have first arg for:
        //result = "./echoArgs.sh \"This is a string that \\\"will be\\\" highlighted\" when your 'regular \"expression' matches something.".executeCommand()
        // first arg = "This is a string that \"will be\" highlighted" when your 'regular "expression'
        // because of greediness of the used Regex if args with single quotes and double quotes are mixed
        result = "./echoArgs.sh \"This is a string that \\\"will be\\\" highlighted\" when your 'regular expression' matches something.".executeCommand()
        log.info { "\n'./echoArgs.sh' result START" }
        log.warn { "result code: ${result.returnCode}" }
        log.warn { echo() ; result.outputLines.joinToString("\n") }
        log.info { "result END" }
        // './echoArgs.sh' result START
        // This is a string that "will be" highlighted
        // when
        // your
        // regular "expression
        // matches
        // something.
        //
        // result END

    }
}
