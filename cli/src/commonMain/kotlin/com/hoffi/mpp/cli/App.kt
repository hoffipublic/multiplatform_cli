package com.hoffi.mpp.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import com.hoffi.mpp.cli.filesystem.fileSystem
import com.hoffi.mpp.cli.mappings.Buildspec
import com.hoffi.mpp.cli.mappings.Nested
import com.hoffi.mpp.common.io.mpp.Console
import com.hoffi.mpp.common.io.mpp.MppProcess
import com.hoffi.mpp.common.io.mpp.ProcessResult
import com.hoffi.mpp.common.io.mpp.executeCommand
import com.hoffi.mpp.common.io.mpp.json.Generic
import com.hoffi.mpp.common.io.mpp.json.Pretty
import com.hoffi.mpp.common.io.mpp.json.collapseParenthesises
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.mamoe.yamlkt.Yaml
import okio.Path.Companion.toPath

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

        mppProcesses()
        jsonYaml()
    }

    fun mppProcesses() {
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

    fun jsonYaml() {
        //val yamlParserConfig = YamlConfiguration(strictMode = false, breakScalarsAt = 120) // sequenceStyle = flow // for [1, 2, 3]

        var input = fileSystem.read("./data/buildspec.yml".toPath()) { readUtf8() }
        val buildspec: Buildspec = Yaml.decodeFromString(Buildspec.serializer(), input)
        println("=== Kotlin:")
        println(buildspec)

        println("====================================================")
        println("extracted: ${buildspec.phases.build.commands.joinToString("', '", "['", "']")}")
        println("single.1: '${buildspec.phases.build.commands[1]}'")
        println("====================================================")

        println("\n\n=== kotlinx pretty:")
        var json: String = Pretty.JSON.encodeToString(buildspec)
        println(json)

        println("\n\n===manually collapsed:")
        println(json.collapseParenthesises())

        println("\n\n=== collapsed pretty:")
        json = Pretty.JSON.encodeToString(buildspec).collapseParenthesises()
        println(json)


        println("\n\n==== complex manual:")
        input = fileSystem.read("./data/nested.json".toPath()) { readUtf8() }
        var nested: Nested =  Json { ignoreUnknownKeys = true }
            .decodeFromString(Nested.serializer(), input)
        println(nested)
        println("\n\n==== complex encodeToString:")
        var s = Pretty.JSON.encodeToString(nested)
        println(s)
        println("\n\n==== complex encodeToString collapsed:")
        s = s.collapseParenthesises()
        println(s)

        println("\n\n=== parseToJsonElement:")
        val jsonMap: MutableMap<String, Any> = Generic.parseToMap(input)
        println(jsonMap)


        println("\n\n========================================")
        println("=== parseToJsonElement: ================")
        println("========================================")
        val jsonInput = fileSystem.read("./data/buildspec.json".toPath()) { readUtf8() }
        val theMap: MutableMap<String, Any> = Generic.parseToMap(jsonInput)
        println(theMap)

        val extracted = Generic.get(theMap, "phases.pre_build.commands.3")
        println("\nextracted single: $extracted")

        println("\nextracted array:")
        val extrArr = Generic.get(theMap, "phases.pre_build.commands") as ArrayList<*>
        extrArr.forEachIndexed { index, any ->  println("${index + 1}: $any") }

        val extrObj = Generic.get(theMap, ".phases.build") as MutableMap<*, *>
        println("\nextrObj: $extrObj")

        println("finished")
    }
}
