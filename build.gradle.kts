import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

plugins {
    // `kotlin-dsl`
    id("java")
    kotlin("jvm") version BuildSrcGlobal.VersionKotlin apply false
    kotlin("multiplatform") version BuildSrcGlobal.VersionKotlin apply false
    kotlin("plugin.serialization") version BuildSrcGlobal.VersionKotlin apply false
    kotlin("kapt") version BuildSrcGlobal.VersionKotlin apply false
    kotlin("plugin.allopen") version BuildSrcGlobal.VersionKotlin apply false
    id("com.github.johnrengelman.shadow") version PluginDeps.PluginShadow.version apply false
    id("idea")
}

group = "com.hoffi"
version = "1.0.0" // apple dmg insists on a version not starting with a '0'
val artifactName by extra { rootProject.name.toLowerCase() }

val repoSsh by extra("git@gitlab.com:???.git")
val repoHttps by extra("https://gitlab.com/???.git")

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = BuildSrcGlobal.JavaLanguageVersion.toString()
                //Will retain parameter names for Java reflection
                javaParameters = true
                //freeCompilerArgs = freeCompilerArgs + listOf(
                //    "--Xjavac-arguments=-Xlint:-deprecation"
                //)
            }
        }
    }
}

// copy all jars to root project's build/libs
val gather = tasks.register<Copy>("gather") {
    mkdir(File(rootProject.buildDir,"libs"))
    val allSubprojectsLibDirs: MutableList<String> = mutableListOf()
    subprojects.forEach { allSubprojectsLibDirs.add("${it.buildDir}/libs") }
    into(project.rootProject.buildDir.toString())
    into("libs") {
        from(allSubprojectsLibDirs)
    }

    mkdir(File(rootProject.buildDir,"bin"))
    val allSubprojectsBinDirs: MutableList<String> = mutableListOf()
    subprojects.forEach { theSubproject ->
        listOf("mac", "unix", "windows").forEach {
            into("bin/${it}") {
                from("${theSubproject.buildDir}/bin/${it}/releaseExecutable")
                include("*.kexe")
            }
        }
    }
}
val build by tasks.existing { finalizedBy(gather) }
// build.finalizedBy publishToMavenLocal // push jars to mavenLocal after build
val clean by tasks.existing { delete(rootProject.buildDir) }


// ################################################################################################
// #####    pure informational stuff on stdout    #################################################
// ################################################################################################
// implemented in buildSrc/src/main/kotlin/Deps.kt
tasks.register<CheckVersionsTask>("checkVersions")
subprojects {
    tasks {
        register("versionsPrint") {
            group = "misc"
            description = "extract spring boot versions from dependency jars"
            doLast {
                val foreground = BuildSrcGlobal.ConsoleColor.YELLOW
                val background = BuildSrcGlobal.ConsoleColor.DEFAULT
                BuildSrcGlobal.printlnColor(foreground, "Gradle version: " + project.gradle.gradleVersion, background)
                BuildSrcGlobal.printColor(foreground, "Kotlin version: " + project.kotlinExtension.coreLibrariesVersion) ; if (project.kotlinExtension.coreLibrariesVersion != BuildSrcGlobal.VersionKotlin) BuildSrcGlobal.printColor(BuildSrcGlobal.ConsoleColor.RED, " ( != ${BuildSrcGlobal.VersionKotlin} )")
                println()
                BuildSrcGlobal.printlnColor(foreground, "javac  version: " + org.gradle.internal.jvm.Jvm.current(), background) // + " with compiler args: " + options.compilerArgs, backgroundColor = BuildSrcGlobal.ConsoleColor.DARK_GRAY)
                BuildSrcGlobal.printlnColor(foreground, "       srcComp: " + java.sourceCompatibility, background)
                BuildSrcGlobal.printlnColor(foreground, "       tgtComp: " + java.targetCompatibility, background)
                BuildSrcGlobal.printlnColor(foreground, "versions of core dependencies:", background)
                val regex = Regex(pattern = "^(spring-cloud-starter|spring-boot-starter|micronaut-core|kotlin-stdlib-jdk[0-9-]+|foundation-desktop)-[0-9].*$")
                if (subprojects.size > 0) {
                    project.configurations.compileClasspath.get().isCanBeResolved = true
                    project.configurations.compileClasspath.get().map { it.nameWithoutExtension }.filter { it.matches(regex) }
                        .forEach { BuildSrcGlobal.printlnColor(foreground, String.format("%-25s: %s", project.name, it), background) }
                } else {
                    project.configurations.compileClasspath.get().isCanBeResolved = true
                    project.configurations.compileClasspath.get().map { it.nameWithoutExtension }.filter { it.matches(regex) }
                        .forEach { BuildSrcGlobal.printlnColor(foreground, "  $it", background) }
                }
            }
        }
    }
    build {
        val versionsPrint by tasks.existing
        finalizedBy(versionsPrint)
    }
}
