tasks.register("versionsPrint") {
    group = "misc"
    description = "extract spring boot versions from dependency jars"
    doLast {
        val foreground = BuildSrcGlobal.ConsoleColor.YELLOW
        val background = BuildSrcGlobal.ConsoleColor.DEFAULT
        val shadowJar by tasks.getting(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class)
        BuildSrcGlobal.printlnColor(foreground, "  fat/uber jar: ${shadowJar.archiveFileName.get()}", background)
        BuildSrcGlobal.printlnColor(foreground, "Gradle version: " + project.gradle.gradleVersion, background)
        BuildSrcGlobal.printColor(foreground, "Kotlin version: " + kotlin.coreLibrariesVersion) ; if (kotlin.coreLibrariesVersion != BuildSrcGlobal.VersionKotlin) BuildSrcGlobal.printColor(
        BuildSrcGlobal.ConsoleColor.RED, " ( != ${BuildSrcGlobal.VersionKotlin} )")
        println()
        BuildSrcGlobal.printlnColor(foreground, "javac  version: " + org.gradle.internal.jvm.Jvm.current(), background) // + " with compiler args: " + options.compilerArgs, backgroundColor = ConsoleColor.DARK_GRAY)
        BuildSrcGlobal.printlnColor(foreground, "       srcComp: " + java.sourceCompatibility, background)
        BuildSrcGlobal.printlnColor(foreground, "       tgtComp: " + java.targetCompatibility, background)
        BuildSrcGlobal.printlnColor(foreground, "versions of core dependencies:", background)
        val regex = Regex(pattern = "^(spring-cloud-starter|spring-boot-starter|micronaut-core|kotlin-stdlib-jdk[0-9-]+|foundation-desktop)-[0-9].*$")
        if (subprojects.size > 0) {
            configurations.compileClasspath.get().map { it.nameWithoutExtension }.filter { it.matches(regex) }
                .forEach { BuildSrcGlobal.printlnColor(foreground, String.format("%-25s: %s", project.name, it), background) }
        } else {
            configurations.compileClasspath.get().map { it.nameWithoutExtension }.filter { it.matches(regex) }
                .forEach { BuildSrcGlobal.printlnColor(foreground, "  $it", background) }
        }
    }
}
val build by tasks.existing {
    val versionsPrint by tasks.existing
    finalizedBy(versionsPrint)
}
//// or within subprojects { ... } or allprojects { ... }
//try {
//    val buildTask = project.tasks.getByPath(":${project.name}:build")
//    val versionsPrint by tasks.existing
//    buildTask.finalizedBy(versionsPrint)
//} catch (ex: UnknownTaskException) {
//    println("-> WARNING -> ${project.name} does not have a 'build' task.")
//    // project.tasks.forEach { println("     ${it.name}") }
//}
