plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "com.hoffi"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        val javaVersion: JavaLanguageVersion by rootProject.extra
        (this as JavaToolchainSpec).languageVersion.set(javaVersion)
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    val hostOS: String by rootProject.extra
    when (hostOS) {
        "MAC" -> macosX64()
        "LINUX" -> linuxX64()
        "WINDOWS" -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native: $hostOS from '${System.getProperty("os.name")}'")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.microutils:kotlin-logging:${Deps.KotlinLogging.version}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:${Deps.KotlinxDatetime.version}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Deps.KotlinxJson.version}")
                //implementation("com.charleskorn.kaml:kaml:${Deps.Misc.KOTLINXYAML.VERSION}")
                implementation("net.mamoe.yamlkt:yamlkt:${Deps.KotlinxYaml.version}")
                implementation("com.squareup.okio:okio:${Deps.Okio.version}")
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
                runtimeOnly("ch.qos.logback:logback-classic") { version { strictly(Deps.Logback.version) } }
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        when (hostOS) {
            "MAC" -> {
                val macosX64Main by getting {
                    dependencies {
                    }}}
            "LINUX" -> {
                val linuxX64Main by getting {
                    dependencies {
                    }}}
            "WINDOWS" -> {
                val mingwX64Main by getting {
                    dependencies {
                    }}}
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }
    }
}
