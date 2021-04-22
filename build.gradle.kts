plugins {
    kotlin("multiplatform") version "1.4.32"
}

group = "com.hoffi"
version = "1.0.0"
val theMainClass by extra { "com.hoffi.web.AppKt" }

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        //withJava() // applies the Gradle java plugin to allow the multiplatform project to have both Java and Kotlin source files (src/jvmMain/java/...)

        // compilations.all {
        //     kotlinOptions.jvmTarget = "1.8"
        // }

        // create an executable java fat jar
        val jvmJar by tasks.getting(org.gradle.jvm.tasks.Jar::class) {
            doFirst {
                // configurations.forEach { println(it.name) }
                manifest {
                    attributes["Main-Class"] = theMainClass
                }
                from(configurations.getByName("jvmRuntimeClasspath").map { if (it.isDirectory) it else zipTree(it) })
            }
        }
    }
    macosX64("mac") { // without name param, creates sourceSet named("macosX64Main") and named("macosX64Main")
        // linuxX64("linux") // on Linux
        // mingwX64("windows") // on Windows
        binaries {
            executable {
                // entry point function = package with non-inside-object main method + ".main" (= name of the main function)
                entryPoint(theMainClass.replaceAfterLast(".", "main"))
                //runTask?.args("")
                // Use the following Gradle tasks to run your application:
                // (<subproject>):runReleaseExecutableMacos - without debug symbols
                // (<subproject>):runDebugExecutableMacos - with debug symbols
            }
        }
    }

    // https://kotlinlang.org/docs/mpp-share-on-platforms.html#share-code-in-libraries
    // To enable usage of platform-dependent libraries in shared source sets, add the following to your `gradle.properties`
    // kotlin.mpp.enableGranularSourceSetsMetadata=true
    // kotlin.native.enableDependencyPropagation=false
    sourceSets {
        val commonMain by getting  { // predefined by gradle multiplatform plugin
            dependencies {
                //implementation("io.github.microutils:kotlin-logging:2.0.6")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
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
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation("org.slf4j:slf4j-api:1.7.30")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val macMain by getting { // named("macMain") {
            dependencies {

            }
        }
        val macTest by getting { // named("macTest") {
            dependencies {

            }
        }
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
