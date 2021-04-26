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
    val DEPS_TO_CHECK = TreeMap<String, Dep>()
    val GRADLE_PLUGINS_TO_CHECK = LinkedList<DepPlugin>()

    object Plugins {
        object Micronaut {
            val VERSION = "1.4.5"
            val micronautPlugin = DepPlugin("Micronaut", VERSION, "io.micronaut.application").also{ GRADLE_PLUGINS_TO_CHECK.add(it) }
        }
        object Shadow {
            val VERSION = "6.1.0"
            val shadowPlugin = DepPlugin("Shadow", VERSION, "com.github.johnrengelman.shadow").also{ GRADLE_PLUGINS_TO_CHECK.add(it) }
        }
    }

    object Tests {
        object Junit {
            // __Junit VERSION__
            val VERSION = "5.7.0"
            val HAMCREST_VERSION = "2.2"
            val GROUPLEADERDEP = Dep.from("org.junit.jupiter:junit-jupiter-api:$VERSION")

            val junitApi = GROUPLEADERDEP
                .also { DEPS_TO_CHECK[Junit.javaClass.simpleName] = it }
            val junitEngine = Dep.from("org.junit.jupiter:junit-jupiter-engine:$VERSION")

            val hamcrestLibrary = Dep.from("org.hamcrest:hamcrest-library:${HAMCREST_VERSION}")
                .also { DEPS_TO_CHECK[Junit.javaClass.simpleName] = it }
        }
    }

    object JetBrains {
        object Kotlin {
            // __KOTLIN_VERSION__
            val VERSION = "1.4.32"
            val GROUPLEADERDEP = Dep.from("org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION")

            val kotlin = GROUPLEADERDEP
                .also { DEPS_TO_CHECK[Kotlin.javaClass.simpleName] = it }

            val gradlePlugin = GROUPLEADERDEP
            val testCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-common:$VERSION")
            val testJunit = Dep.from("org.jetbrains.kotlin:kotlin-test-junit:$VERSION")
            val testAnnotationsCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-annotations-common:$VERSION")
        }

        object Compose {
            // __LATEST_COMPOSE_RELEASE_VERSION__
            // https://github.com/JetBrains/compose-jb/releases
            val VERSION = "0.4.0-build182"// https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/compose-gradle-plugin/maven-metadata.xml
            val GROUPLEADERDEP = Dep.from("org.jetbrains.compose:compose-gradle-plugin:$VERSION", repo = JETBRAINS)
                .also { DEPS_TO_CHECK[Compose.javaClass.simpleName] = it }

            val gradlePlugin = GROUPLEADERDEP
        }

        object Exposed {
            val VERSION = "0.30.2"
            val GROUPLEADERDEP = Dep.from("org.jetbrains.exposed:exposed-core:$VERSION")
                .also { DEPS_TO_CHECK[Exposed.javaClass.simpleName] = it}

            val jetbrainsExposedSQL = GROUPLEADERDEP
        }
    }

    object Micronaut {
        object BOM {
            val VERSION = "2.4.2"
            val GROUPLEADERDEP = Dep.from("io.micronaut:micronaut-bom:$VERSION")
                .also { DEPS_TO_CHECK[Micronaut.javaClass.simpleName] = it }

            val micronautBom = GROUPLEADERDEP
        }
    }

    object DB {
        object Postgresql {
            val VERSION = "42.2.19"
            val GROUPLEADERDEP = Dep.from("org.postgresql:postgresql:$VERSION", versionRegex = "${THREEDIGITSs}\\.jre\\d*\$")
                .also { DEPS_TO_CHECK[Postgresql.javaClass.simpleName] = it }

            val postgresJdbc = GROUPLEADERDEP
        }
        object H2 {
            val VERSION = "1.4.200"
            val GROUPLEADERDEP = Dep.from("com.h2database:h2:${VERSION}")
                .also { DEPS_TO_CHECK[H2.javaClass.simpleName] = it }

            val h2Jdbc = GROUPLEADERDEP.toString()
        }
    }

    object Apache {
        val poiVersion = "4.1.2"
        val poi = Dep.from("org.apache.poi:poi:$poiVersion")
            .also { DEPS_TO_CHECK["Poi"] = it }
        val poiOoxml = Dep.from("org.apache.poi:poi-ooxml:$poiVersion")

        val antlrVersion = "4.9.2"
        val antlr = Dep.from("org.antlr:antlr4:$antlrVersion")
            .also { DEPS_TO_CHECK["ANTLR"] = it }
    }

    object Logging {
        val logbackVersion = "1.2.3"
        val logback = Dep.from("ch.qos.logback:logback-classic:$logbackVersion")
            .also { DEPS_TO_CHECK["logback"] = it }

        val slf4j_VERSION = "1.7.30"
        val slf4jApi = Dep.from("org.slf4j:slf4j-api:${slf4j_VERSION}")
            .also { DEPS_TO_CHECK[Logging.javaClass.simpleName] = it }

    }

    object Eclipse {
        object EMF {
            val ecoreVersion = "2.22.0"
            val ecoreXmiVersion = "2.16.0"
            val ecore = Dep.from("org.eclipse.emf:org.eclipse.emf.ecore:$ecoreVersion")
                .also { DEPS_TO_CHECK["ecore"] = it }
            val ecoreXmi = Dep.from("org.eclipse.emf:org.eclipse.emf.ecore.xmi:$ecoreXmiVersion")
                .also { DEPS_TO_CHECK["ecore"] = it }
        }
    }

    object Web {
        object KHTTP {
            val VERSION = "1.0.0"
            val GROUPLEADERDEP = Dep.from("khttp:khttp:$VERSION", JCENTER)
                .also { DEPS_TO_CHECK[KHTTP.javaClass.simpleName] = it }
        }
        object MISC {
            val JSOUP_VERSION = "1.13.1"
            val jsoup = Dep.from("org.jsoup:jsoup:${JSOUP_VERSION}")
                .also { DEPS_TO_CHECK["webmisc"] = it }
        }
    }

    object Android {
        object Tools {
            object Build {
                val VERSION = "4.1.2"
                val GROUPLEADERDEP = Dep.from("com.android.tools.build:gradle:$VERSION", GOOGLE)
                    .also { DEPS_TO_CHECK[Build.javaClass.simpleName] = it }

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
            val VERSION = "2.0.2"
            val GROUPLEADERDEP = Dep.from("com.arkivanov.mvikotlin:mvikotlin:$VERSION", repo = JCENTER)
                .also { DEPS_TO_CHECK[MVIKotlin.javaClass.simpleName] = it }

            val mvikotlin = GROUPLEADERDEP
            val rx = Dep.from("com.arkivanov.mvikotlin:rx:$VERSION", repo = JCENTER)
            val mvikotlinMain = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main:$VERSION", repo = JCENTER)
            val mvikotlinMainIosX64 = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main-iosx64:$VERSION", repo = JCENTER)
            val mvikotlinMainIosArm64 = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main-iosarm64:$VERSION", repo = JCENTER)
            val mvikotlinLogging = Dep.from("com.arkivanov.mvikotlin:mvikotlin-logging:$VERSION", repo = JCENTER)
            val mvikotlinTimeTravel = Dep.from("com.arkivanov.mvikotlin:mvikotlin-timetravel:$VERSION", repo = JCENTER)
            val mvikotlinExtensionsReaktive = Dep.from("com.arkivanov.mvikotlin:mvikotlin-extensions-reaktive:$VERSION", repo = JCENTER)
        }

        object Decompose {
            val VERSION = "0.2.3"
            val GROUPLEADERDEP = Dep.from("com.arkivanov.decompose:decompose:$VERSION")
                .also { DEPS_TO_CHECK[Decompose.javaClass.simpleName] = it}

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
                .also { DEPS_TO_CHECK[Reaktive.javaClass.simpleName] = it}

            val reaktive = GROUPLEADERDEP
            val reaktiveTesting = Dep.from("com.badoo.reaktive:reaktive-testing:$VERSION")
            val utils = Dep.from("com.badoo.reaktive:utils:$VERSION")
            val coroutinesInterop = Dep.from("com.badoo.reaktive:coroutines-interop:$VERSION")
        }
    }

    object Squareup {
        object SQLDelight {
            val VERSION = "1.4.4"
            val GROUPLEADERDEP = Dep.from("com.squareup.sqldelight:gradle-plugin:$VERSION")
                .also { DEPS_TO_CHECK[SQLDelight.javaClass.simpleName] = it}


            val gradlePlugin = GROUPLEADERDEP
            val androidDriver = Dep.from("com.squareup.sqldelight:android-driver:$VERSION")
            val sqliteDriver = Dep.from("com.squareup.sqldelight:sqlite-driver:$VERSION")
            val nativeDriver = Dep.from("com.squareup.sqldelight:native-driver:$VERSION")
        }
    }
}
