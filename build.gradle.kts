plugins {
    // `kotlin-dsl`
    id("java")
    kotlin("jvm") version Deps.JetBrains.Kotlin.VERSION apply false
    kotlin("multiplatform") version Deps.JetBrains.Kotlin.VERSION apply false
    kotlin("plugin.serialization") version Deps.JetBrains.Kotlin.VERSION apply false
    kotlin("kapt") version Deps.JetBrains.Kotlin.VERSION apply false
    kotlin("plugin.allopen") version Deps.JetBrains.Kotlin.VERSION apply false
    id("com.github.johnrengelman.shadow") version Deps.Plugins.Shadow.VERSION apply false
    id("idea")
}

group = "com.hoffi"
version = "1.0.0" // apple dmg insists on a version not starting with a '0'
val artifactName by extra { rootProject.name.toLowerCase() }

val repoSsh by extra("git@gitlab.com:???.git")
val repoHttps by extra("https://gitlab.com/???.git")

// implemented in buildSrc/src/main/kotlin/Deps.kt
tasks.register<CheckVersionsTask>("checkVersions")

allprojects {
    repositories {
        mavenCentral()
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
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
