import Dep.Companion.THREEDIGITSs
import java.net.URL

const val UNSPECIFIED = "UNSPECIFIED"

object Deps {
    val APPLIED_DEPS = sortedMapOf<String, MutableSet<Dep>>().toMutableMap()
    val APPLIED_PLUGINS = mutableListOf<DepPlugin>()


    object Plugins {
        val Micronaut = DepPlugin("Micronaut", "io.micronaut.application", VERSION ="2.0.7").also { it.add() }
        val Shadow = DepPlugin("Shadow", "com.github.johnrengelman.shadow", VERSION = "7.1.0").also { it.add() }
    }

    object Tests {
        var VERSION_Junit = UNSPECIFIED
        val Junit = Dep.from("org.junit.jupiter:junit-jupiter-api:5.7.0").also { VERSION_Junit = it.version ; it.add() }
        val JunitEngine = Dep.from("org.junit.jupiter:junit-jupiter-engine:$VERSION_Junit")

        val hamcrestLibrary = Dep.from("org.hamcrest:hamcrest-library:2.2").also { it.add() }
        val Kotest = Dep.from("io.kotest:kotest-runner-junit5-jvm:4.6.3").also { it.add() }
    }

    object JetBrains {
        var VERSION_Kotlin = UNSPECIFIED
        val Kotlin = Dep.from("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31").also { VERSION_Kotlin = it.version ; it.add() }
        val KotlinTestCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-common:$VERSION_Kotlin")
        val KotlinTestJunit = Dep.from("org.jetbrains.kotlin:kotlin-test-junit:$VERSION_Kotlin")
        val KotlinTestAnnotationsCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-annotations-common:$VERSION_Kotlin")

        // __LATEST_COMPOSE_RELEASE_VERSION__
        // https://github.com/JetBrains/compose-jb/releases
        val Compose = Dep.from("org.jetbrains.compose:compose-gradle-plugin:1.0.0-beta1", repo = JETBRAINS).also { it.add() }

        val Exposed = Dep.from("org.jetbrains.exposed:exposed-core:0.35.3").also { it.add() }
    }

    object Core {
        val Arrow  = Dep.from("io.arrow-kt:arrow-core:1.0.0").also { it.add() }
    }

    object Micronaut {
        var VERSION_Micronaut = UNSPECIFIED
        val Bom = Dep.from("io.micronaut:micronaut-bom:3.1.1").also { VERSION_Micronaut = it.version ; it.add() }
    }

    object DB {
        val Postgresql = Dep.from("org.postgresql:postgresql:42.3.0", versionRegex = "${THREEDIGITSs}\\.jre\\d*\$").also { it.add() }
        val H2 = Dep.from("com.h2database:h2:1.4.200").also { it.add() }
    }

    object Apache {
        var VERSION_Apache_Poi = UNSPECIFIED
        val Poi = Dep.from("org.apache.poi:poi:4.1.2").also { VERSION_Apache_Poi = it.version ; it.add() }
        val PoiOoxml = Dep.from("org.apache.poi:poi-ooxml:$VERSION_Apache_Poi")

        var VERSION_Apache_Antlr = UNSPECIFIED
        val Antlr = Dep.from("org.antlr:antlr4:4.9.2").also { VERSION_Apache_Antlr = it.version ; it.add() }
    }

    object Logging {
        var VERSION_Logback = UNSPECIFIED
        val Logback = Dep.from("ch.qos.logback:logback-classic:1.2.6").also { VERSION_Logback = it.version ; it.add() }
        var VERSION_Slf4j = UNSPECIFIED
        val Slf4jApi = Dep.from("org.slf4j:slf4j-api:1.7.32").also { VERSION_Slf4j = it.version ; it.add() }

    }

    object Eclipse {
        var VERSION_Ecore = UNSPECIFIED
        var VERSION_ecoreXmi = UNSPECIFIED
        val Ecore = Dep.from("org.eclipse.emf:org.eclipse.emf.ecore:2.22.0").also { VERSION_Ecore = it.version ; it.add() }
        val EcoreXmi = Dep.from("org.eclipse.emf:org.eclipse.emf.ecore.xmi:2.16.0").also { VERSION_ecoreXmi = it.version ; it.add() }
    }

    object Web {
        var VERSION_Ktor = UNSPECIFIED
        val Ktor = Dep.from("io.ktor:ktor-server-core:1.6.6").also { VERSION_Ktor = it.version ; it.add() }
        val Khttp = Dep.from("khttp:khttp:1.0.0", JCENTER).also { it.add() }
        val Sslcontext = Dep.from("io.github.hakky54:sslcontext-kickstart:7.0.3").also { it.add() }
        val jsoup = Dep.from("org.jsoup:jsoup:1.13.1").also { it.add() }
    }

    object Squareup {
        val Okhttp = Dep.from("com.squareup.okhttp3:okhttp:4.9.2").also { it.add() }
        val Retrofit = Dep.from("com.squareup.retrofit2:retrofit:2.9.0").also { it.add() }
        val Moshi = Dep.from("com.squareup.moshi:moshi-kotlin:1.12.0").also { it.add() }
        val Okio = Dep.from("com.squareup.okio:okio:2.10.0").also { it.add() }
        var VERSION_SQLDelight = UNSPECIFIED
        val SQLDelight = Dep.from("com.squareup.sqldelight:gradle-plugin:1.4.4").also { VERSION_SQLDelight = it.version ; it.add() }
        val androidDriver = Dep.from("com.squareup.sqldelight:android-driver:$VERSION_SQLDelight")
        val sqliteDriver = Dep.from("com.squareup.sqldelight:sqlite-driver:$VERSION_SQLDelight")
        val nativeDriver = Dep.from("com.squareup.sqldelight:native-driver:$VERSION_SQLDelight")
    }

    object Misc {
        val KotlinxDatetime = Dep.from("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1").also { it.add() }
        val Clikt = Dep.from("com.github.ajalt.clikt:clikt:3.3.0").also { it.add() }
        val Koodies = Dep.from("com.bkahlert.koodies:koodies:1.9.7").also { it.add() }
    }
    object Serialization {
        val KotlinxJson = Dep.from("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1").also { it.add() }
        val KotlinxYaml = Dep.from("net.mamoe.yamlkt:yamlkt:0.10.0").also { it.add() }
        val KotlinxKaml = Dep.from("com.charleskorn.kaml:kaml:0.36.0").also { it.add() }
        val Snakeyaml = Dep.from("org.yaml:snakeyaml:1.29").also { it.add() }
    }

    object Android {
        val Tools = Dep.from("com.android.tools.build:gradle:4.1.2", GOOGLE).also { it.add() }
    }

    object AndroidX {
        val AppCompatUrl = java.net.URI("https://maven.google.com/web/index.html?q=androidx.appco#androidx.appcompat:appcompat")
        val AppCompat = Dep.from("androidx.appcompat:appcompat:1.2.0").also { it.add() }
    }

    object ArkIvanov {
        var VERSION_MVIKotlin = UNSPECIFIED
        val MVIKotlin = Dep.from("com.arkivanov.mvikotlin:mvikotlin:2.0.4").also { VERSION_MVIKotlin = it.version ; it.add() }
        val MVIKotlinRX = Dep.from("com.arkivanov.mvikotlin:rx:$VERSION_MVIKotlin")
        val MVIkotlinMain = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main:$VERSION_MVIKotlin")
        val MVIkotlinMainIosX64 = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main-iosx64:$VERSION_MVIKotlin")
        val MVIkotlinMainIosArm64 = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main-iosarm64:$VERSION_MVIKotlin")
        val MVIkotlinLogging = Dep.from("com.arkivanov.mvikotlin:mvikotlin-logging:$VERSION_MVIKotlin")
        val MVIkotlinTimeTravel = Dep.from("com.arkivanov.mvikotlin:mvikotlin-timetravel:$VERSION_MVIKotlin")
        val MVIkotlinExtensionsReaktive = Dep.from("com.arkivanov.mvikotlin:mvikotlin-extensions-reaktive:$VERSION_MVIKotlin")

        var VERSION_Decompose = UNSPECIFIED
        val Decompose = Dep.from("com.arkivanov.decompose:decompose:0.4.0").also { VERSION_Decompose = it.version ; it.add() }
        val DecomposeIosX64 = Dep.from("com.arkivanov.decompose:decompose-iosx64:$VERSION_Decompose")
        val DecomposeIosArm64 = Dep.from("com.arkivanov.decompose:decompose-iosarm64:$VERSION_Decompose")
        val extensionsCompose = Dep.from("com.arkivanov.decompose:extensions-compose-jetbrains:$VERSION_Decompose")
    }

    object Badoo {
        var VERSION_Badoo = UNSPECIFIED
        val Reaktive = Dep.from("com.badoo.reaktive:reaktive:1.1.19").also { VERSION_Badoo = it.version ; it.add() }
        val ReaktiveTesting = Dep.from("com.badoo.reaktive:reaktive-testing:$VERSION_Badoo")
        val utils = Dep.from("com.badoo.reaktive:utils:$VERSION_Badoo")
        val coroutinesInterop = Dep.from("com.badoo.reaktive:coroutines-interop:$VERSION_Badoo")
    }
}

data class Dep(
    val group: String,
    val name: String,
    var version: String = UNSPECIFIED,
    val repo: Repo = MAVENCENTRAL,
    val versionRegex: String? = null
) {
    constructor(groupAndName: String, version: String = UNSPECIFIED, repo: Repo = MAVENCENTRAL, versionRegex: String? = null):
            this(groupAndName.split(':')[0], groupAndName.split(':')[1], version, repo, versionRegex)
    override fun toString(): String = groupAndArtifact() + if(version == UNSPECIFIED) "" else ":${version}"
    fun full(): String = "$group:$name:$version"
    fun groupAndArtifact(): String = "$group:$name"
    fun toDirPath(): String = "${group.replace('.', '/')}/$name"
    fun mavenMetadataXmlURL(): URL = repo.mavenMetadataXmlURL(this)
    fun checkVersion(remoteVersion: String?): Boolean {
        if (remoteVersion == null) return false
        return if (versionRegex == null) { remoteVersion == version } else { versionRegex.toRegex().matches(remoteVersion) }
    }

    fun add() { Deps.APPLIED_DEPS.getOrPut(this::class.java.packageName){setOf(this).toMutableSet()} }

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

data class DepPlugin(val name: String, val id: String, val VERSION: String) {
    fun add() { Deps.APPLIED_PLUGINS.add(this) }
}

