# Kotlin multiplatform terminal cli example

simple terminal cmd with multiplatform args parsing that is executable on jvm/java or as native compiled kotlin on Mac/OSX, Linux and Windows/Mingw

<br/>
<big>
Multiplatform subproject `:lib` <b>posix exec's arbitrary processes on native platforms</b><br/>
and returns stdout/stderr as a kotlin <small>`String?`</small>
</big> 
<br/><br/>

**executable on the folloging targets:**

* jvm
* macosX64
* linuxX64
* mingwX64 (unverified)


## main class

`./cli/src/commonMain/kotlin/com/hoffi/mpp/cli/App.kt`

## dependencies

* https://ajalt.github.io/clikt/
* https://github.com/Kotlin/kotlinx-datetime

## restrictions

cannot set working dir (it is always `./`)

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

