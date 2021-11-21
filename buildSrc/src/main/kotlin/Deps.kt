import Dep.Companion.THREEDIGITSs
import java.net.URL
import java.util.*

const val UNSPECIFIED = "UNSPECIFIED"

data class Dep(
    val group: String,
    val name: String,
    var version: String = UNSPECIFIED,
    val repo: Repo = MAVENCENTRAL,
    val versionRegex: String? = null
) {
    override fun toString(): String = groupAndArtifact() + if(version == UNSPECIFIED) "" else ":${version}"
    fun full(): String = "$group:$name:$version"
    fun groupAndArtifact(): String = "$group:$name"
    fun toDirPath(): String = "${group.replace('.', '/')}/$name"
    fun mavenMetadataXmlURL(): URL = repo.mavenMetadataXmlURL(this)
    fun checkVersion(remoteVersion: String?): Boolean {
        if (remoteVersion == null) return false
        return if (versionRegex == null) { remoteVersion == version } else { versionRegex.toRegex().matches(remoteVersion) }
    }

    companion object {
        val THREEDIGITSs = "^(\\d+\\.)?(\\d+\\.)?(\\*|\\d+)"
        val THREEDIGITS = Regex("$THREEDIGITSs\$")
        fun from(depString: String, repo: Repo = MAVENCENTRAL, versionRegex: String? = null): Dep {
            val parts = depString.split(':')
            if (!(2..3).contains(parts.size)) throw Exception("gradle dependency syntax error on '${depString}'")
            return when (parts.size) {
                3 -> Dep(parts[0], parts[1], parts[2], repo, versionRegex)
                else -> Dep(parts[0], parts[1], UNSPECIFIED, repo, versionRegex)
            }
        }
    }
}
data class DepPlugin(val name: String, val VERSION: String, val id: String)

object Deps {
    val APPLIED_DEPS = TreeMap<String, Dep>()
    val APPLIED_PLUGINS = LinkedList<DepPlugin>()

    object Plugins {
        object Micronaut {
            val VERSION = "2.0.7"
            val micronautPlugin = DepPlugin("Micronaut", VERSION, "io.micronaut.application")
                .also{ APPLIED_PLUGINS.add(it) }
        }
        object Shadow {
            val VERSION = "7.1.0"
            val shadowPlugin = DepPlugin("Shadow", VERSION, "com.github.johnrengelman.shadow")
                .also{ APPLIED_PLUGINS.add(it) }
        }
    }

    object Tests {
        object Junit {
            // __Junit VERSION__
            val VERSION = "5.7.0"
            val HAMCREST_VERSION = "2.2"
            val GROUPLEADERDEP = Dep.from("org.junit.jupiter:junit-jupiter-api:$VERSION")

            val junitApi = GROUPLEADERDEP
                .also { APPLIED_DEPS[Junit.javaClass.simpleName] = it }
            val junitEngine = Dep.from("org.junit.jupiter:junit-jupiter-engine:$VERSION")

            val hamcrestLibrary = Dep.from("org.hamcrest:hamcrest-library:${HAMCREST_VERSION}")
                .also { APPLIED_DEPS[Junit.javaClass.simpleName] = it }
        }
        object Kotest {
            val VERSION = "4.6.3"
            val GROUPLEADER = Dep.from("io.kotest:kotest-runner-junit5-jvm:${VERSION}")
            val kotest = GROUPLEADER
                .also { APPLIED_DEPS[Kotest.javaClass.simpleName] = it }
        }
    }

    object JetBrains {
        object Kotlin {
            // __KOTLIN_VERSION__
            val VERSION = "1.5.31"
            val GROUPLEADERDEP = Dep.from("org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION")

            val kotlin = GROUPLEADERDEP
                .also { APPLIED_DEPS[Kotlin.javaClass.simpleName] = it }

            val gradlePlugin = GROUPLEADERDEP
            val testCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-common:$VERSION")
            val testJunit = Dep.from("org.jetbrains.kotlin:kotlin-test-junit:$VERSION")
            val testAnnotationsCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-annotations-common:$VERSION")
        }

        object Compose {
            // __LATEST_COMPOSE_RELEASE_VERSION__
            // https://github.com/JetBrains/compose-jb/releases
            val VERSION = "1.0.0-beta1"
            val GROUPLEADERDEP = Dep.from("org.jetbrains.compose:compose-gradle-plugin:$VERSION", repo = JETBRAINS)
                .also { APPLIED_DEPS[Compose.javaClass.simpleName] = it }

            val gradlePlugin = GROUPLEADERDEP
        }

        object Exposed {
            val VERSION = "0.35.3"
            val GROUPLEADERDEP = Dep.from("org.jetbrains.exposed:exposed-core:$VERSION")
                .also { APPLIED_DEPS[Exposed.javaClass.simpleName] = it}

            val jetbrainsExposedSQL = GROUPLEADERDEP
        }
    }

    object Core {
        object Arrow {
            val VERSION = "1.0.0"
            val dep = Dep.from("io.arrow-kt:arrow-core:$VERSION")
                .also { APPLIED_DEPS[Arrow.javaClass.simpleName] = it }
        }
    }

    object Micronaut {
        object BOM {
            val VERSION = "3.1.1"
            val GROUPLEADERDEP = Dep.from("io.micronaut:micronaut-bom:$VERSION")
                .also { APPLIED_DEPS[Micronaut.javaClass.simpleName] = it }

            val micronautBom = GROUPLEADERDEP
        }
    }

    object DB {
        object Postgresql {
            val VERSION = "42.3.0"
            val GROUPLEADERDEP = Dep.from("org.postgresql:postgresql:$VERSION", versionRegex = "${THREEDIGITSs}\\.jre\\d*\$")
                .also { APPLIED_DEPS[Postgresql.javaClass.simpleName] = it }

            val postgresJdbc = GROUPLEADERDEP
        }
        object H2 {
            val VERSION = "1.4.200"
            val GROUPLEADERDEP = Dep.from("com.h2database:h2:${VERSION}")
                .also { APPLIED_DEPS[H2.javaClass.simpleName] = it }

            val h2Jdbc = GROUPLEADERDEP.toString()
        }
    }

    object Apache {
        val poiVersion = "4.1.2"
        val poi = Dep.from("org.apache.poi:poi:$poiVersion")
            .also { APPLIED_DEPS["Poi"] = it }
        val poiOoxml = Dep.from("org.apache.poi:poi-ooxml:$poiVersion")

        val antlrVersion = "4.9.2"
        val antlr = Dep.from("org.antlr:antlr4:$antlrVersion")
            .also { APPLIED_DEPS["ANTLR"] = it }
    }

    object Logging {
        val logbackVersion = "1.2.6"
        val logback = Dep.from("ch.qos.logback:logback-classic:$logbackVersion")
            .also { APPLIED_DEPS["logback"] = it }

        val slf4j_VERSION = "1.7.32"
        val slf4jApi = Dep.from("org.slf4j:slf4j-api:${slf4j_VERSION}")
            .also { APPLIED_DEPS[Logging.javaClass.simpleName] = it }

        val kotlinLogging_VERSION = "2.0.12"
        val kotlinLogging = Dep.from("io.github.microutils:kotlin-logging:${kotlinLogging_VERSION}")
            .also { APPLIED_DEPS[Logging.javaClass.simpleName] = it }
    }

    object Eclipse {
        object EMF {
            val ecoreVersion = "2.22.0"
            val ecoreXmiVersion = "2.16.0"
            val ecore = Dep.from("org.eclipse.emf:org.eclipse.emf.ecore:$ecoreVersion")
                .also { APPLIED_DEPS["ecore"] = it }
            val ecoreXmi = Dep.from("org.eclipse.emf:org.eclipse.emf.ecore.xmi:$ecoreXmiVersion")
                .also { APPLIED_DEPS["ecore"] = it }
        }
    }

    object Web {
        object KTOR {
            val VERSION = "1.6.3"
            val ktor = Dep.from("io.ktor:ktor-server-core:${VERSION}")
                .also { APPLIED_DEPS[KTOR.javaClass.simpleName] = it }
        }
        object KHTTP {
            val VERSION = "1.0.0"
            val GROUPLEADERDEP = Dep.from("khttp:khttp:$VERSION", JCENTER)
                .also { APPLIED_DEPS[KHTTP.javaClass.simpleName] = it }
        }
        object SSLCONTEXT {
            val VERSION = "7.0.0"
            val sslcontext = Dep.from("io.github.hakky54:sslcontext-kickstart:${VERSION}")
                .also { APPLIED_DEPS[SSLCONTEXT.javaClass.simpleName] = it }
        }
        object MISC {
            val JSOUP_VERSION = "1.13.1"
            val jsoup = Dep.from("org.jsoup:jsoup:${JSOUP_VERSION}")
                .also { APPLIED_DEPS["webmisc"] = it }
        }
    }

    object Squareup {
        object OKHTTP {
            val VERSION = "4.9.2"
            val okhttp = Dep.from("com.squareup.okhttp3:okhttp:${VERSION}")
                .also { APPLIED_DEPS[OKHTTP.javaClass.simpleName] = it }
        }
        object RETROFIT {
            val VERSION = "2.9.0"
            val retrofit = Dep.from("com.squareup.retrofit2:retrofit:${VERSION}")
                .also { APPLIED_DEPS[RETROFIT.javaClass.simpleName] = it }
        }
        object MOSHI {
            val VERSION = "1.12.0"
            val moshi = Dep.from("com.squareup.moshi:moshi-kotlin:${VERSION}")
                .also { APPLIED_DEPS[MOSHI.javaClass.simpleName] = it }
        }
        object OKIO {
            val VERSION = "3.0.0"
            val okio = Dep.from("com.squareup.okio:okio:${VERSION}")
                .also { APPLIED_DEPS[OKIO.javaClass.simpleName] = it }
        }
        object SQLDelight {
            val VERSION = "1.4.4"
            val GROUPLEADERDEP = Dep.from("com.squareup.sqldelight:gradle-plugin:$VERSION")
                .also { APPLIED_DEPS[SQLDelight.javaClass.simpleName] = it }


            val gradlePlugin = GROUPLEADERDEP
            val androidDriver = Dep.from("com.squareup.sqldelight:android-driver:$VERSION")
            val sqliteDriver = Dep.from("com.squareup.sqldelight:sqlite-driver:$VERSION")
            val nativeDriver = Dep.from("com.squareup.sqldelight:native-driver:$VERSION")
        }
    }

    object Misc {
        object DATETIME {
            val VERSION = "0.3.1"
            val datetime = Dep.from("org.jetbrains.kotlinx:kotlinx-datetime:$VERSION")
                .also { APPLIED_DEPS[DATETIME.javaClass.simpleName] = it }
        }
        object KOTLINXJSON {
            val VERSION = "1.3.1"
            val yamlVersion = "0.10.0"
            val kotlinxJson = Dep.from("org.jetbrains.kotlinx:kotlinx-serialization-json:$VERSION")
                .also { APPLIED_DEPS[KOTLINXJSON.javaClass.simpleName] = it }
            val kotlinxYaml = Dep.from("net.mamoe.yamlkt:yamlkt:${yamlVersion}")
        }
        object KOTLINXYAML {
            val VERSION = "0.36.0"
            val kotlinxJson = Dep.from("com.charleskorn.kaml:kaml:$VERSION")
                .also { APPLIED_DEPS[KOTLINXYAML.javaClass.simpleName] = it }
        }
        object KOTLINXYAMLKT {
            val VERSION = "0.10.2"
            val kotlinxJson = Dep.from("net.mamoe.yamlkt:yamlkt:$VERSION")
                .also { APPLIED_DEPS[KOTLINXYAMLKT.javaClass.simpleName] = it }
        }
        object CLIKT {
            val VERSION = "3.3.0"
            val clikt = Dep.from("com.github.ajalt.clikt:clikt:$VERSION")
                .also { APPLIED_DEPS[CLIKT.javaClass.simpleName] = it }
        }
        object KOODIES {
            val VERSION = "1.9.7"
            val koodies = Dep.from("com.bkahlert.koodies:koodies:$VERSION")
                .also { APPLIED_DEPS[KOODIES.javaClass.simpleName] = it }
        }
        object SNAKEYAML {
            val VERSION = "1.29"
            val snakeyaml = Dep.from("org.yaml:snakeyaml:$VERSION")
                .also { APPLIED_DEPS[SNAKEYAML.javaClass.simpleName] = it }
        }
    }

    object Android {
        object Tools {
            object Build {
                val VERSION = "4.1.2"
                val GROUPLEADERDEP = Dep.from("com.android.tools.build:gradle:$VERSION", GOOGLE)
                    .also { APPLIED_DEPS[Build.javaClass.simpleName] = it }

                val gradlePlugin = GROUPLEADERDEP.toString()
            }
        }
    }

    object AndroidX {
        object AppCompat {
            val url = java.net.URI("https://maven.google.com/web/index.html?q=androidx.appco#androidx.appcompat:appcompat")
            const val appCompat = "androidx.appcompat:appcompat:1.2.0"
        }
    }

    object ArkIvanov {
        object MVIKotlin {
            val VERSION = "2.0.4"
            val GROUPLEADERDEP = Dep.from("com.arkivanov.mvikotlin:mvikotlin:$VERSION")
                .also { APPLIED_DEPS[MVIKotlin.javaClass.simpleName] = it }

            val mvikotlin = GROUPLEADERDEP
            val rx = Dep.from("com.arkivanov.mvikotlin:rx:$VERSION")
            val mvikotlinMain = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main:$VERSION")
            val mvikotlinMainIosX64 = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main-iosx64:$VERSION")
            val mvikotlinMainIosArm64 = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main-iosarm64:$VERSION")
            val mvikotlinLogging = Dep.from("com.arkivanov.mvikotlin:mvikotlin-logging:$VERSION")
            val mvikotlinTimeTravel = Dep.from("com.arkivanov.mvikotlin:mvikotlin-timetravel:$VERSION")
            val mvikotlinExtensionsReaktive = Dep.from("com.arkivanov.mvikotlin:mvikotlin-extensions-reaktive:$VERSION")
        }

        object Decompose {
            val VERSION = "0.4.0"
            val GROUPLEADERDEP = Dep.from("com.arkivanov.decompose:decompose:$VERSION")
                .also { APPLIED_DEPS[Decompose.javaClass.simpleName] = it}

            val decompose = GROUPLEADERDEP
            val decomposeIosX64 = Dep.from("com.arkivanov.decompose:decompose-iosx64:$VERSION")
            val decomposeIosArm64 = Dep.from("com.arkivanov.decompose:decompose-iosarm64:$VERSION")
            val extensionsCompose = Dep.from("com.arkivanov.decompose:extensions-compose-jetbrains:$VERSION")
        }
    }

    object Badoo {
        object Reaktive {
            val VERSION = "1.1.19"
            val GROUPLEADERDEP = Dep.from("com.badoo.reaktive:reaktive:$VERSION")
                .also { APPLIED_DEPS[Reaktive.javaClass.simpleName] = it}

            val reaktive = GROUPLEADERDEP
            val reaktiveTesting = Dep.from("com.badoo.reaktive:reaktive-testing:$VERSION")
            val utils = Dep.from("com.badoo.reaktive:utils:$VERSION")
            val coroutinesInterop = Dep.from("com.badoo.reaktive:coroutines-interop:$VERSION")
        }
    }
}
