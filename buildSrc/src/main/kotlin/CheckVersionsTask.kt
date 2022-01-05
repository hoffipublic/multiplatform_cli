import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/** only dependencies that are actually used are checked, otherwise the `.also { DEPS_TO_CHECK["xxx"] = it }` clause won't be executed */
open class CheckVersionsTask : DefaultTask() {
//    @Input
//    lateinit var some: String

    @TaskAction
    fun checkDepVersions() {
        val gradleVersionRegex = Regex(".*Gradle ([^ \\n\\r]+).*", RegexOption.MULTILINE)
        val gradleShellVersion = runCatching { "gradle --version".runCommand()?.let { gradleVersionRegex.find(it)?.value } ?: "unknown" }.onFailure { "shell gradle not installed" }.getOrElse { "not installed locally" }
        val gradlewVersion  =    runCatching { "${project.rootProject.projectDir}/gradlew --version".runCommand()?.let { gradleVersionRegex.find(it)?.value } ?: "unknown" }.onFailure { "no gradlew" }.getOrElse { "unknown" }
        if (gradleShellVersion != "not installed locally") {
            if (gradleShellVersion != gradlewVersion) println("mismatch: gradle shell ${gradleShellVersion}\nvs local wrapper $gradlewVersion")
        }
        println("actually used dependencies to check: ${Deps.APPLIED_DEPS.flatMap { it.value }.size}")
        for (depEntries in Deps.APPLIED_DEPS) {
            println("${depEntries.key}:")
            for (dep in depEntries.value) {
                try {
                    val text = dep.mavenMetadataXmlURL().readText()
                    val regexLatest = Regex("<latest>(.*)</latest>", RegexOption.MULTILINE)
                    val regexRelease = Regex("<release>(.*)</release>", RegexOption.MULTILINE)
                    val regexVersions = Regex(".*<version>(.*)</version>", setOf(RegexOption.MULTILINE, RegexOption.UNIX_LINES))
                    val latestMatchResult = regexLatest.find(text)
                    val releaseMatchResult = regexRelease.find(text)
                    val versionsSet = regexVersions.findAll(text)
                    val lastVersion = versionsSet.mapNotNull{ match -> match.groupValues[1] }.findLast{
                        ! it.contains(Regex("(alpha|beta|\\d\\d\\d\\d-\\d\\d-\\d\\d)"))
                    }

                    print(String.format("  %-20s current: %-14s", dep.artifact, dep.version))
                    var latest = "not found"
                    var release = "not found"
                    if(latestMatchResult != null) latest = latestMatchResult.groupValues[1]
                    if(releaseMatchResult != null) release = releaseMatchResult.groupValues[1]

                    if(dep.checkVersion(release) || dep.checkVersion(lastVersion))  {
                        print(" UP-TO-DATE")
                    } else {
                        print(String.format(" latest: %-15s release: %-15s lastVersionRef: %-15s\n    %s", latest.take(19), release.take(19), lastVersion, dep.mavenMetadataXmlURL()))
                    }
                    //print("    ${depLeader.mavenMetadataXMLURL}")
                } catch(ex : Exception) {
                    println(String.format("%-15s: %-29s current: %-16s  remote URL error", dep, dep.artifact, dep.version))
                    print(" tried: ${dep.mavenMetadataXmlURL()}")
                }
                println("")
            }
        }

        println("\nGradle Plugins:\n===============")
        for(depPlugin in Deps.APPLIED_PLUGINS) {
            val regexLatest = Regex("Version (.*) \\(latest\\)", setOf(RegexOption.MULTILINE, RegexOption.UNIX_LINES))
            val url = "https://plugins.gradle.org/plugin/${depPlugin.id}"
            val text = java.net.URL(url).readText()
            // println(text)
            val latestMatchResult = regexLatest.find(text)
            var latest = "not found"
            if(latestMatchResult != null) latest = latestMatchResult.groupValues[1]
            print(String.format("%-29s: current: %-19s", depPlugin.name, depPlugin.VERSION))
            if(depPlugin.VERSION == latest) {
                print(" up-to-date")
            } else {
                print(String.format(" latest: %-19s", latest))
            }
            print("    ${url}")
            println("")
        }
    }
}
