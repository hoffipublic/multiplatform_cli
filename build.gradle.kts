import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

plugins {
    // `kotlin-dsl`
    kotlin("jvm") version BuildSrcGlobal.VersionKotlin apply false
    kotlin("multiplatform") version BuildSrcGlobal.VersionKotlin
    id("io.kotest.multiplatform") version "io.kotest:kotest-runner-junit5".depVersionOnly() apply false
    kotlin("plugin.serialization") version BuildSrcGlobal.VersionKotlin apply false
    kotlin("kapt") version BuildSrcGlobal.VersionKotlin apply false
    kotlin("plugin.allopen") version BuildSrcGlobal.VersionKotlin apply false
    id("com.github.johnrengelman.shadow") version "shadow".pluginVersion() apply false
    application
    id("idea")
}

group = "com.hoffi"
version = "1.0.0" // apple dmg insists on a version not starting with a '0'
val artifactName: String by extra { "${rootProject.name.toLowerCase()}-${project.name.toLowerCase()}" }
val repoSsh by extra("git@gitlab.com:???.git")
val repoHttps by extra("https://gitlab.com/???.git")

//val rootPackage: String by extra { "${rootProject.group}.${rootProject.name.toLowerCase()}" }
val rootPackage: String by extra { "${rootProject.group}.mppcli" }
val projectPackage: String by extra { rootPackage }
val theMainClass: String by extra { "Main" }
application {
    mainClass.set("${rootPackage}.${theMainClass}" + "Kt") // + "Kt" if fun main is outside a class
}

allprojects {
    //println("> root/build.gradle.kts allprojects: $project")
    repositories {
        //mavenLocal()
        mavenCentral()
    }
}

subprojects {
    //println("> root/build.gradle.kts subprojects for: sub$project")
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        println("root/build.gradle.kts subprojects { configuring sub$project as kotlin(\"jvm\") project }")
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        println("root/build.gradle.kts subprojects { configuring sub$project as kotlin(\"multiplatform\") project }")
    }

    tasks {
        register("versionsPrint") {
            group = "misc"
            description = "extract spring boot versions from dependency jars"
            doLast {
                val foreground = BuildSrcGlobal.ConsoleColor.YELLOW
                val background = BuildSrcGlobal.ConsoleColor.DEFAULT
                BuildSrcGlobal.printlnColor(foreground, "Gradle version: " + project.gradle.gradleVersion, background)
                // import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
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
    try {
        val buildTask = project.tasks.getByPath(":${project.name}:build")
        val versionsPrint by tasks.existing
        buildTask.finalizedBy(versionsPrint)
    } catch (ex: UnknownTaskException) {
        println("-> WARNING -> ${project.name} does not have a 'build' task.")
        // project.tasks.forEach { println("     ${it.name}") }
    }
}


kotlin {
    jvmToolchain(BuildSrcGlobal.jdkVersion)
    val nativeTarget = when(BuildSrcGlobal.hostOS) {
        BuildSrcGlobal.HOSTOS.MACOS -> macosX64("native")
        BuildSrcGlobal.HOSTOS.LINUX -> linuxX64("native")
        BuildSrcGlobal.HOSTOS.WINDOWS -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "${rootPackage}.${theMainClass.toLowerCase()}"
            }
        }
    }
    jvm {
        jvmToolchain(BuildSrcGlobal.jdkVersion)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val nativeMain by getting
        val nativeTest by getting
        val jvmMain by getting
        val jvmTest by getting
        val commonMain by getting { // predefined by gradle multiplatform plugin
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                //implementation(kotlin("test-junit"))
            }
        }
    }
}

val run by tasks.existing {
    doFirst {
        throw GradleException("please do not run root project, instead run e.g. ./gradlew cli:run")
    }
}

// copy all jars to root project's build/libs
val gather = tasks.register<Copy>("gather") {
    mkdir(File(rootProject.buildDir, "libs"))
    val allSubprojectsLibDirs: MutableList<String> = mutableListOf()
    subprojects.forEach { allSubprojectsLibDirs.add("${it.buildDir}/libs") }
    into(project.rootProject.buildDir.toString())
    into("libs") {
        from(allSubprojectsLibDirs)
    }

    mkdir(File(rootProject.buildDir, "bin"))
    subprojects.forEach { theSubproject ->
        listOf("macosX64", "linuxX64", "mingwX64").forEach { target ->
            into("bin/${target}") {
                from("${theSubproject.buildDir}/bin/${target}/releaseExecutable")
                include("*.kexe")
            }
        }
    }
}
val build by tasks.existing { dependsOn(":cli:build") ; finalizedBy(gather) }
// build.finalizedBy publishToMavenLocal // push jars to mavenLocal after build
val clean by tasks.existing { delete(rootProject.buildDir) }

// ################################################################################################
// #####    pure informational stuff on stdout    #################################################
// ################################################################################################

tasks.register<CheckVersionsTask>("checkVersions") { // implemented in buildSrc/src/main/kotlin/CheckVersionsTask.kt
    scope = "used" // all
}
apply(from = "buildSrc/snippets/printClasspathMPP.gradle.kts")


// ==============================================================================
// ======   Helpers and pure informational stuff not necessary for build ========
// ==============================================================================

tasks.register("setup") {
    dependsOn(createIntellijScopeSentinels, createSrcBasePackages)
}
// from ./buildSrc/snippets/createSrcBasePackages.kts
val createSrcBasePackages = tasks.register("createSrcBasePackages") {
    doLast {
        project.subprojects.forEach { prj ->
            var relProjectDirString = prj.projectDir.toString().removePrefix(rootProject.projectDir.toString())
            if (relProjectDirString.isBlank()) { relProjectDirString = "ROOT" } else { relProjectDirString = relProjectDirString.removePrefix("/") }
            println("  in project: $relProjectDirString ...")
            val projectPackage: String by prj.extra
            val projectPackageDirString = projectPackage.split('.').joinToString("/")
            prj.pluginManager.let() { when {
                it.hasPlugin("org.jetbrains.kotlin.jvm") -> {
                    prj.sourceSets.forEach { sourceSet ->
                        val ssDir = File("${prj.projectDir}/src/${sourceSet.name}/kotlin")
                        if (ssDir.exists()) {
                            mkdir("$ssDir/$projectPackageDirString")
                        }
                    }
                }
                it.hasPlugin("org.jetbrains.kotlin.multiplatform") -> {
                    prj.kotlin.sourceSets.forEach { sourceSet ->
                        val ssDir = File("${prj.projectDir}/src/${sourceSet.name}/kotlin")
                        if (ssDir.exists()) {
                            mkdir("$ssDir/$projectPackageDirString")
                        }
                    }
                }
            }}
            println("  in project: $relProjectDirString ok.")
        }
    }
}
// from ./buildSrc/snippets/createIntellijScopeSentinels.kts
val createIntellijScopeSentinels = tasks.register("createIntellijScopeSentinels") {
    doLast {
        project.allprojects.forEach { prj ->
            var relProjectDirString = prj.projectDir.toString().removePrefix(rootProject.projectDir.toString())
            if (relProjectDirString.isBlank()) { relProjectDirString = "ROOT" } else { relProjectDirString = relProjectDirString.removePrefix("/") }
            println("  in project: $relProjectDirString ...")
            val suffix = if (prj.name == rootProject.name) {
                "ROOT"
            } else {
                prj.name.toUpperCase()
            }
            prj.pluginManager.let { when {
                it.hasPlugin("org.jetbrains.kotlin.jvm") -> {
                    if (prj.name != rootProject.name) {
                        val dir = mkdir("${prj.projectDir}/01__$suffix")
                        File(dir, ".gitkeep").createNewFile()
                        File(prj.projectDir, "ZZ__$suffix").createNewFile()
                    }
                    prj.sourceSets.forEach { ss: SourceSet ->
                        val ssDir = if (prj.name == rootProject.name) {
                            File("src/${ss.name}")
                        } else {
                            File("${prj.projectDir}/src/${ss.name}")
                        }
                        if (ssDir.exists()) {
                            val mName = ss.name.capitalize()
                            val dir = mkdir("$ssDir/_src${mName}_$suffix")
                            File(dir, ".gitkeep").createNewFile()
                            File(ssDir, "ZZsrc${mName}_$suffix").createNewFile()
                        }
                    }
                }
                it.hasPlugin("org.jetbrains.kotlin.multiplatform") -> {
                    if (prj.name != rootProject.name) {
                        val dir = mkdir("${prj.projectDir}/01__$suffix")
                        File(dir, ".gitkeep").createNewFile()
                        File(prj.projectDir, "ZZ__$suffix").createNewFile()
                    }
                    prj.kotlin.sourceSets.forEach { sourceSet ->
                        val ssDir = if (prj.name == rootProject.name) {
                            File("src/${sourceSet.name}")
                        } else {
                            File("${prj.projectDir}/src/${sourceSet.name}")
                        }
                        if (ssDir.exists()) {
                            if (sourceSet.name.endsWith("Main")) {
                                val mName = sourceSet.name.removeSuffix("Main").capitalize()
                                val dir = mkdir("$ssDir/_src${mName}_$suffix")
                                File(dir, ".gitkeep").createNewFile()
                                File(ssDir, "ZZsrc${mName}_$suffix").createNewFile()
                            }
                        }
                    }
                }
            }}
            println("  in project: $relProjectDirString ok.")
        }
    }
}
