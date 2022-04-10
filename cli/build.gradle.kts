plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    application
}

group = "${rootProject.group}"
version = "${rootProject.version}"
val artifactName by extra { "${rootProject.name.toLowerCase()}-${project.name.toLowerCase()}" }
val theMainClass by extra { "com.hoffi.mpp.cli.AppKt" }

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(BuildSrcGlobal.JavaLanguageVersion)
        vendor.set(BuildSrcGlobal.jvmVendor)
    }
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    when (BuildSrcGlobal.hostOS) {
        BuildSrcGlobal.HOSTOS.MAC -> macosX64() {
            binaries {
                executable {
                    // entry point function = package with non-inside-object main method + ".main" (= name of the main function)
                    entryPoint(theMainClass.replaceAfterLast(".", "main"))
                }
            }
        }
        BuildSrcGlobal.HOSTOS.LINUX -> linuxX64() {
            binaries {
                executable {
                    // entry point function = package with non-inside-object main method + ".main" (= name of the main function)
                    entryPoint(theMainClass.replaceAfterLast(".", "main"))
                }
            }
        }
        BuildSrcGlobal.HOSTOS.WINDOWS -> mingwX64() {
            binaries {
                executable {
                    // entry point function = package with non-inside-object main method + ".main" (= name of the main function)
                    entryPoint(theMainClass.replaceAfterLast(".", "main"))
                }
            }
        }
        else -> throw GradleException("Host OS is not supported in Kotlin/Native: ${BuildSrcGlobal.hostOS} from '${System.getProperty("os.name")}'")
    }
    sourceSets {
        val commonMain by getting  { // predefined by gradle multiplatform plugin
            dependencies {
                implementation(project(":lib"))
                implementation("io.github.microutils:kotlin-logging:${Deps.KotlinLogging.version}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Deps.KotlinxDatetime.version}")
                //api(platform("com.squareup.okio:okio-bom:3.0.0"))
                implementation("com.squareup.okio:okio:${Deps.Okio.version}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Deps.KotlinxJson.version}")
                //implementation("com.charleskorn.kaml:kaml:${Deps.Misc.KOTLINXYAML.VERSION}")
                implementation("net.mamoe.yamlkt:yamlkt:${Deps.KotlinxYaml.version}")

                implementation("com.github.ajalt.clikt:clikt:${Deps.Clikt.version}")
            }
        }
        val commonTest by getting {
            dependencies {
            }
        }

        val jvmMain by getting {
            //print("${name} dependsOn: ")
            //println(dependsOn.map { it.name }.joinToString())
            dependencies {
                runtimeOnly("ch.qos.logback:logback-classic") { version { strictly(Deps.Logback.version) } }
                //implementation("org.slf4j:slf4j-api:1.7.30")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        when (BuildSrcGlobal.hostOS) {
            BuildSrcGlobal.HOSTOS.MAC -> {
                val macosX64Main by getting {
                    dependencies {
            }}}
            BuildSrcGlobal.HOSTOS.LINUX -> {
                val linuxX64Main by getting {
                    dependencies {
            }}}
            BuildSrcGlobal.HOSTOS.WINDOWS -> {
                val mingwX64Main by getting {
                    dependencies {
            }}}
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }
    }
}

application {
    mainClass.set(theMainClass)
}

tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}

tasks {
    val shadowCreate by creating(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        manifest {
            attributes["Main-Class"] = theMainClass
        }
        mergeServiceFiles()

        archiveClassifier.set("fat")
        from(kotlin.jvm().compilations.getByName("main").output)
        configurations =
            mutableListOf(kotlin.jvm().compilations.getByName("main").compileDependencyFiles)
    }
    val build by existing {
        dependsOn(shadowCreate)
    }
}

// ################################################################################################
// #####    pure informational stuff on stdout    #################################################
// ################################################################################################
tasks.register("printClasspath") {
    group = "misc"
    description = "print classpath"
    doLast {
        //project.getConfigurations().filter { it.isCanBeResolved }.forEach {
        //    println(it.name)
        //}
        //println()
        val targets = listOf(
            "metadataCommonMainCompileClasspath",
            "commonMainApiDependenciesMetadata",
            "commonMainImplementationDependenciesMetadata",
            "jvmCompileClasspath",
            "kotlinCompilerClasspath"
        )
        targets.forEach { targetConfiguration ->
            println("$targetConfiguration:")
            println("=".repeat("$targetConfiguration:".length))
            project.getConfigurations()
                .getByName(targetConfiguration).files
                // filters only existing and non-empty dirs
                .filter { (it.isDirectory() && it.listFiles().isNotEmpty()) || it.isFile() }
                .forEach { println(it) }
        }
    }
}
