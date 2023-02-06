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
