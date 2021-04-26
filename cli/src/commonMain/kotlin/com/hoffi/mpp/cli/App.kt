package com.hoffi.mpp.cli

import com.hoffi.mpp.common.io.MppProcess
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import com.hoffi.mpp.common.io.executeCommand
import com.hoffi.mpp.log.Logger

fun main(args: Array<String>) {
    App().main(args)
}

class App : CliktCommand() {
    val log = Logger(this::class)
    val count: Int by option(help="Number of greetings").int().default(3)
    val name: String by option(help="The person to greet").prompt("Your name")

    val greeting = "should have a greeting"

    override fun run() {
        echo("echo '--count ${count} times '--name=${name}':")
        (1..count).forEach { i ->
            echo("${i}: Hello $name!")
        }
        val cmd = "ls -ghFp"
        var result = MppProcess.executeCommand(cmd)
        println("\n'${cmd}' result START")
        println(result)
        println("result END")

        // jvm will erroneously have first arg for:
        //result = "./echoArgs.sh \"This is a string that \\\"will be\\\" highlighted\" when your 'regular \"expression' matches something.".executeCommand()
        // first arg = "This is a string that \"will be\" highlighted" when your 'regular "expression'
        // because of greediness of the used Regex if args with single quotes and double quotes are mixed
        result = "./echoArgs.sh \"This is a string that \\\"will be\\\" highlighted\" when your 'regular expression' matches something.".executeCommand()
        println("\n'./echoArgs.sh' result START")
        println(result)
        println("result END")
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
