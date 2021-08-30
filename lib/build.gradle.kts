plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "com.hoffi"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    val hostOs = System.getProperty("os.name")
    var posixHost = false
    var windowsHost = false
    // val isMingwX64 = hostOs.startsWith("Windows")
    // val nativeTarget = when {
    //     hostOs == "Mac OS X" -> macosX64("native")
    //     hostOs == "Linux" -> linuxX64("native")
    //     isMingwX64 -> mingwX64("native")
    //     else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    // }
    if (hostOs == "Mac OS X" || hostOs == "Linux" ) {
        posixHost = true
        macosX64()
        linuxX64()
    } else if (hostOs.startsWith("Windows")) {
        windowsHost = true
        mingwX64()
    } else throw GradleException("Host OS is not supported in Kotlin/Native.")

    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        if (posixHost) {
            val macosX64Main by getting {
                dependencies {
                }
            }
            val linuxX64Main by getting {
                dependencies {
                }
            }
        }
        if (windowsHost) {
            val mingwX64Main by getting {
                dependencies {
                }
            }
        }
    }
}
