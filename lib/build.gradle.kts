plugins {
    kotlin("multiplatform")
    id("io.kotest.multiplatform")
    kotlin("plugin.serialization")
}

group = "${rootProject.group}"
version = "${rootProject.version}"
val artifactName by extra { "${rootProject.name.toLowerCase()}-${project.name.toLowerCase()}" }
val rootPackage: String by rootProject.extra
val projectPackage: String by extra { "${rootPackage}.${project.name.toLowerCase()}" }
//val theMainClass by extra { "com.hoffi.mpp.cli.AppKt" }
//application {
//    mainClass.set(theMainClass)
//}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(BuildSrcGlobal.jdkVersion)
    jvm {
        testRuns["test"].executionTask.configure {
            buildSrcJvmTestConfig()
        }
    }
    when (BuildSrcGlobal.hostOS) {
        BuildSrcGlobal.HOSTOS.MACOS   -> macosX64()
        BuildSrcGlobal.HOSTOS.LINUX   -> linuxX64()
        BuildSrcGlobal.HOSTOS.WINDOWS -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native: ${BuildSrcGlobal.hostOS} from '${System.getProperty("os.name")}'")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //implementation(libs.kotlinx.coroutines.core)
                implementation("io.github.microutils:kotlin-logging".depAndVersion())
                implementation("org.jetbrains.kotlinx:kotlinx-datetime".depAndVersion())
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json".depAndVersion())
                //implementation("com.charleskorn.kaml:kaml".depAndVersion())
                implementation("net.mamoe.yamlkt:yamlkt".depAndVersion())
                implementation("com.squareup.okio:okio".depAndVersion())
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine".depButVersionOf("io.kotest:kotest-runner-junit5"))
                implementation("io.kotest:kotest-framework-datatest".depButVersionOf("io.kotest:kotest-runner-junit5"))
                implementation("io.kotest:kotest-assertions-core".depButVersionOf("io.kotest:kotest-runner-junit5"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

            }
        }
        val jvmMain by getting {
            dependencies {
                runtimeOnly("ch.qos.logback:logback-classic") { version { strictly("ch.qos.logback:logback-classic".depVersionOnly()) } }
            }
        }
        val jvmTest by getting {
            dependencies {
                //implementation(kotlin("test-junit"))
                //runtimeOnly("org.junit.jupiter:junit-jupiter-engine".depAndVersion())
                runtimeOnly("io.kotest:kotest-runner-junit5".depAndVersion()) // depends on jvm { useJUnitPlatform() } // Platform!!! nd not only useJUnit()
            }
        }
        when (BuildSrcGlobal.hostOS) {
            BuildSrcGlobal.HOSTOS.MACOS -> {
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

tasks {
    withType(org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest::class) {
        buildSrcCommonTestConfig("NATIVE")
        // listen to standard out and standard error of the test JVM(s)
        // onOutput { descriptor, event -> logger.lifecycle("Test: " + descriptor + " produced standard out/err: " + event.message ) }
    }
}
