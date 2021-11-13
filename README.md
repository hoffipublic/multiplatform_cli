# Kotlin multiplatform terminal shell/bash cli scripting library and example

This is a kotlin native gradle Multiplatform MultiProject.

It demonstrates how to write platform independent bash shell scripting with pure kotlin.

project `:cli` is a jvm/macosX64/linuxX64/Windows-MingwX64 [clikt](https://ajalt.github.io/clikt/) based command line client

project `:lib` is a jvm/macosX64/linuxX64/Windows-MingwX64 library for:

- executing (bash based) shell scripts on any above platform and provide their output to kotlin code
  - output of executed commands can also be `tee`'d to stdout 
  - output of executed commands can be `tee`'d with a beautifull console frame around the output
- console readLine for input and echo methods for output on all above platforms


<big><bold>
executable on the following target platforms:
</bold></big>

* jvm (uses ProcessBuilder("/bin/bash", "-c", commandString))
* macosX64
* linuxX64
* Windows mingwX64 (unverified)


## main class

`./cli/src/commonMain/kotlin/com/hoffi/mpp/cli/App.kt`

## restrictions

set working dir not implemented yet (it is always `./`)

## build

`./gradlew clean build`

## run

be sure to run from project root as `./echoArgs.sh` is there.

```
cli.kexe --help
Usage: app [OPTIONS]

Options:
  --count INT  Number of greetings
  --name TEXT  The person to greet
  -h, --help   Show this message and exit
```

### java jvm

`java -jar cli/build/libs/cli-1.0.0-fat.jar --name jvm --count=5`

### mac / linux / windows

```
cli/build/bin/mac/releaseExecutable/cli.kexe     --name Mac
cli/build/bin/linux/releaseExecutable/cli.kexe   --name Linux
cli/build/bin/windows/releaseExecutable/cli.kexe --name Windows
```

## example output

```kotlin
MppProcess.executeCommandFramed("""
    echo "this xxx the first line" | sed 's/xxx/is/'
    echo "some fancy second line" 'with both quotes' \
         over multiple lines
""".trimIndent(), echoCmdToErr = true, teeStdout = true)
```

```
╭╴echo "this xxx the first line" | sed 's/xxx/is/'
│ echo "some fancy second line" 'with both quotes' \
│      over multiple lines
├───────────────────────────────────────────────────
│ this is the first line
│ some fancy second line with both quotes over multiple lines
╰────────────────────────────────────────────────────────────
```


```kotlin
val userinput = Console.inputLine("Console input: ")
Console.echoErr("<stderr> input was: $userinput")
```

```
Console input: asdf
stderr: input was: asdf
```

```kotlin
val cmd = """
    ls -lsh \
       -Fp | awk "match(\${'$'}0, /^(.*)(${'$'}(whoami))(.*)${'$'}/, m) { print m[1] \"redacted\" m[3]; next };1"
    echo "multiple cmds possible"

val result: ProcessResult = MppProcess.executeCommandFramed(cmd, echoCmdToErr = true)
log.warn { "result code: ${result.returnCode}" }
...
""".trimIndent()
```

```
╭╴ls -lsh \
│    -Fp | awk "match(\$0, /^(.*)($(whoami))(.*)$/, m) { print m[1] \"redacted\" m[3]; next };1"
│ echo "multiple cmds possible"
├───────────────────────────────────────────────────────────────────────────────────────────────
│ total 64
│  8 -rw-r--r--@ 1 redacted  staff   1.3K Apr 26  2021 README.md
│  0 drwxr-xr-x  6 redacted  staff   192B Nov 13 16:18 build/
│  8 -rw-r--r--  1 redacted  staff   2.3K Nov 10 22:21 build.gradle.kts
│  0 drwxr-xr-x@ 8 redacted  staff   256B Aug 31 13:18 buildSrc/
│  0 drwxr-xr-x  5 redacted  staff   160B Nov 13 12:24 cli/
│  8 -rwxr-xr-x  1 redacted  staff    61B Apr 26  2021 echoArgs.sh
│  0 drwxr-xr-x  3 redacted  staff    96B Apr 23  2021 gradle/
│  8 -rw-r--r--  1 redacted  staff   124B Apr 23  2021 gradle.properties
│ 16 -rwxr-xr-x  1 redacted  staff   7.9K Aug 30 22:20 gradlew
│  8 -rw-r--r--  1 redacted  staff   2.7K Aug 30 22:20 gradlew.bat
│  0 drwxr-xr-x  5 redacted  staff   160B Nov 13 12:24 lib/
│  8 -rw-r--r--  1 redacted  staff    74B Apr 25  2021 settings.gradle.kts
│ multiple cmds possible
╰───────────────────────────────────────────────────────────────────────────────────────────────
16:20:02.722 [main] WARN com.hoffi.mpp.cli.App - result code: 0
```

```kotlin
val result: ProcessResult = MppProcess.executeCommandFramed(cmd, echoCmdToErr = false)
```

```
╭╴total 64
│  8 -rw-r--r--@ 1 redacted  staff   1.3K Apr 26  2021 README.md
│  0 drwxr-xr-x  6 redacted  staff   192B Nov 13 16:18 build/
│  8 -rw-r--r--  1 redacted  staff   2.3K Nov 10 22:21 build.gradle.kts
│  0 drwxr-xr-x@ 8 redacted  staff   256B Aug 31 13:18 buildSrc/
│  0 drwxr-xr-x  5 redacted  staff   160B Nov 13 12:24 cli/
│  8 -rwxr-xr-x  1 redacted  staff    61B Apr 26  2021 echoArgs.sh
│  0 drwxr-xr-x  3 redacted  staff    96B Apr 23  2021 gradle/
│  8 -rw-r--r--  1 redacted  staff   124B Apr 23  2021 gradle.properties
│ 16 -rwxr-xr-x  1 redacted  staff   7.9K Aug 30 22:20 gradlew
│  8 -rw-r--r--  1 redacted  staff   2.7K Aug 30 22:20 gradlew.bat
│  0 drwxr-xr-x  5 redacted  staff   160B Nov 13 12:24 lib/
│  8 -rw-r--r--  1 redacted  staff    74B Apr 25  2021 settings.gradle.kts
│ multiple cmds possible
╰─────────────────────────────────────────────────────────────────────────
```

```kotlin
val result: ProcessResult = MppProcess.executeCommand(cmd, echoCmdToErr = true)
```

```
-> ls -lsh \
   -Fp | awk "match(\$0, /^(.*)($(whoami))(.*)$/, m) { print m[1] \"redacted\" m[3]; next };1"
echo "multiple cmds possible"
total 64
 8 -rw-r--r--@ 1 redacted  staff   1.3K Apr 26  2021 README.md
 0 drwxr-xr-x  6 redacted  staff   192B Nov 13 16:18 build/
 8 -rw-r--r--  1 redacted  staff   2.3K Nov 10 22:21 build.gradle.kts
 0 drwxr-xr-x@ 8 redacted  staff   256B Aug 31 13:18 buildSrc/
 0 drwxr-xr-x  5 redacted  staff   160B Nov 13 12:24 cli/
 8 -rwxr-xr-x  1 redacted  staff    61B Apr 26  2021 echoArgs.sh
 0 drwxr-xr-x  3 redacted  staff    96B Apr 23  2021 gradle/
 8 -rw-r--r--  1 redacted  staff   124B Apr 23  2021 gradle.properties
16 -rwxr-xr-x  1 redacted  staff   7.9K Aug 30 22:20 gradlew
 8 -rw-r--r--  1 redacted  staff   2.7K Aug 30 22:20 gradlew.bat
 0 drwxr-xr-x  5 redacted  staff   160B Nov 13 12:24 lib/
 8 -rw-r--r--  1 redacted  staff    74B Apr 25  2021 settings.gradle.kts
multiple cmds possible
```

```kotlin
val result: ProcessResult = MppProcess.executeCommand(cmd, echoCmdToErr = false)
val output: String = result.outputLines.joinToString("\n")
```
