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

var javaVersion by extra(JavaLanguageVersion.of(11))
var posixHost by extra(false)
val hostOS by extra { with(System.getProperty("os.name").toLowerCase()) { when  {
    indexOf("win") >= 0 -> "WINDOWS"
    indexOf("mac") >= 0 -> { posixHost = true ; "MAC" }
    indexOf("nix") >= 0 || indexOf("nux") >= 0 || indexOf("aix") > 0 -> { posixHost = true ; "LINUX" }
    else -> throw GradleException("Host OS is not supported in Kotlin/Native: '${System.getProperty("os.name")}'")
}}}

allprojects {
    repositories {
        mavenLocal()
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


// implemented in buildSrc/src/main/kotlin/Deps.kt
tasks.register<CheckVersionsTask>("checkVersions")
