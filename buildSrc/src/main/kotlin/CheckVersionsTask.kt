import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/** only dependencies that are actually used are checked, otherwise the `.also { DEPS_TO_CHECK["xxx"] = it }` clause won't be executed */
open class CheckVersionsTask : DefaultTask() {
//    @Input
//    lateinit var some: String

    @TaskAction
    fun checkDepVersions() {
        val gradleVersionRegex = Regex(".*Gradle ([^ \\n\\r]+).*", RegexOption.MULTILINE)
        val gradleShellVersion = "gradle --version".runCommand()?.let { gradleVersionRegex.find(it)?.value } ?: "unknown"
        val gradlewVersion  = "${project.rootProject.projectDir}/gradlew --version".runCommand()?.let { gradleVersionRegex.find(it)?.value } ?: "unknown"
        if (gradleShellVersion != gradlewVersion) println("mismatch:  shell ${gradleShellVersion}\nvs local wrapper ${gradlewVersion}")

        println("actually used dependencies to check: ${Deps.APPLIED_DEPS.size}")
        for (depEntry in Deps.APPLIED_DEPS) {
            val dep = depEntry.value
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

                print(String.format("%-15s: %-29s current: %-16s", depEntry.key, dep.name, dep.version))
                var latest = "not found"
                var release = "not found"
                if(latestMatchResult != null) latest = latestMatchResult.groupValues[1]
                if(releaseMatchResult != null) release = releaseMatchResult.groupValues[1]

                if(dep.checkVersion(release) || dep.checkVersion(lastVersion))  {
                    print(" UP-TO-DATE")
                } else {
                    print(String.format(" latest: %-19s release: %-19s lastVersionRef: %-19s (%s)", latest.take(19), release.take(19), lastVersion, dep.mavenMetadataXmlURL()))
                }
                //print("    ${depLeader.mavenMetadataXMLURL}")
            } catch(ex : Exception) {
                println(String.format("%-15s: %-29s current: %-16s  remote URL error", depEntry.key, dep.name, dep.version))
                print(" tried: ${dep.mavenMetadataXmlURL()}")
            }
            println("")
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
