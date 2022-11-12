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
    jvmToolchain(BuildSrcGlobal.jdkVersion)
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    when (BuildSrcGlobal.hostOS) {
        BuildSrcGlobal.HOSTOS.MAC     -> macosX64()
        BuildSrcGlobal.HOSTOS.LINUX   -> linuxX64()
        BuildSrcGlobal.HOSTOS.WINDOWS -> mingwX64()
        else -> throw GradleException("Host OS is not supported in Kotlin/Native: ${BuildSrcGlobal.hostOS} from '${System.getProperty("os.name")}'")
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
