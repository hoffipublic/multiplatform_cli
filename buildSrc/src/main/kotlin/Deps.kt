import Dep.Companion.THREEDIGITSs
import java.net.URL

const val UNSPECIFIED = "UNSPECIFIED"

object Deps {
    val APPLIED_DEPS = sortedMapOf<String, MutableSet<Dep>>().toMutableMap()
    val APPLIED_PLUGINS = mutableListOf<DepPlugin>()

    fun add(dep: Dep, key: Any): Dep { APPLIED_DEPS.getOrPut(key.javaClass.simpleName){ emptySet<Dep>().toMutableSet()}.add(dep) ; return dep }

    class Plugins { companion object {
        val Micronaut = DepPlugin("Micronaut", "io.micronaut.application", VERSION = "3.1.1").also { it.add() }
        val Shadow = DepPlugin("Shadow", "com.github.johnrengelman.shadow", VERSION = "7.1.2").also { it.add() }
    }}

    class Tests { companion object {
        var VERSION_Junit = UNSPECIFIED
        val Junit = Dep.from("org.junit.jupiter:junit-jupiter-api:5.8.2").also { VERSION_Junit = it.version ; add(it, Tests) }
        val JunitEngine = Dep.from("org.junit.jupiter:junit-jupiter-engine:$VERSION_Junit")

        val hamcrestLibrary = Dep.from("org.hamcrest:hamcrest-library:2.2").also { add(it, Tests) }
        val Kotest = Dep.from("io.kotest:kotest-runner-junit5-jvm:5.0.3").also { add(it, Tests) }
    }}

    class JetBrains { companion object {
        var VERSION_Kotlin = UNSPECIFIED
        val Kotlin = Dep.from("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10").also { VERSION_Kotlin = it.version ; add(it, JetBrains) }
        val KotlinTestCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-common:$VERSION_Kotlin")
        val KotlinTestJunit = Dep.from("org.jetbrains.kotlin:kotlin-test-junit:$VERSION_Kotlin")
        val KotlinTestAnnotationsCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-annotations-common:$VERSION_Kotlin")

        // __LATEST_COMPOSE_RELEASE_VERSION__
        // https://github.com/JetBrains/compose-jb/releases
        val Compose = Dep.from("org.jetbrains.compose:compose-gradle-plugin:1.0.1", repo = JETBRAINS).also { add(it, JetBrains) }

        val Exposed = Dep.from("org.jetbrains.exposed:exposed-core:0.37.3").also { add(it, JetBrains) }
    }}

    class Core { companion object {
        val Arrow  = Dep.from("io.arrow-kt:arrow-core:1.0.1").also { add(it, Core) }
    }}

    class Micronaut { companion object {
        var VERSION_Micronaut = UNSPECIFIED
        val Bom = Dep.from("io.micronaut:micronaut-bom:3.2.4").also { VERSION_Micronaut = it.version ; add(it, Micronaut) }
    }}

    class DB { companion object {
        val Postgresql = Dep.from("org.postgresql:postgresql:42.3.1", versionRegex = "${THREEDIGITSs}\\.jre\\d*\$").also { add(it, DB) }
        val H2 = Dep.from("com.h2database:h2:2.0.206").also { add(it, DB) }
    }}

    class Apache { companion object {
        var VERSION_Apache_Poi = UNSPECIFIED
        val Poi = Dep.from("org.apache.poi:poi:4.1.2").also { VERSION_Apache_Poi = it.version ; add(it, Apache) }
        val PoiOoxml = Dep.from("org.apache.poi:poi-ooxml:$VERSION_Apache_Poi")

        var VERSION_Apache_Antlr = UNSPECIFIED
        val Antlr = Dep.from("org.antlr:antlr4:4.9.3").also { VERSION_Apache_Antlr = it.version ; add(it, Apache) }
    }}

    class Logging { companion object {
        var VERSION_Logback = UNSPECIFIED
        val Logback = Dep.from("ch.qos.logback:logback-classic:1.2.9").also { VERSION_Logback = it.version ; add(it, Logging) }
        var VERSION_Slf4j = UNSPECIFIED
        val Slf4jApi = Dep.from("org.slf4j:slf4j-api:1.7.32").also { VERSION_Slf4j = it.version ; add(it, Logging) }
        val KotlinLogging = Dep.from("io.github.microutils:kotlin-logging:2.1.21").also { add(it, Logging) }
    }}

    class Eclipse { companion object {
        var VERSION_Ecore = UNSPECIFIED
        var VERSION_ecoreXmi = UNSPECIFIED
        val Ecore = Dep.from("org.eclipse.emf:org.eclipse.emf.ecore:2.22.0").also { VERSION_Ecore = it.version ; add(it, Eclipse) }
        val EcoreXmi = Dep.from("org.eclipse.emf:org.eclipse.emf.ecore.xmi:2.16.0").also { VERSION_ecoreXmi = it.version ; add(it, Eclipse) }
    }}

    class Web { companion object {
        var VERSION_Ktor = UNSPECIFIED
        val Ktor = Dep.from("io.ktor:ktor-server-core:1.6.7").also { VERSION_Ktor = it.version ; add(it, Web) }
        val Khttp = Dep.from("khttp:khttp:1.0.0", JCENTER).also { add(it, Web) }
        val Sslcontext = Dep.from("io.github.hakky54:sslcontext-kickstart:7.1.0").also { add(it, Web) }
        val jsoup = Dep.from("org.jsoup:jsoup:1.14.3").also { add(it, Web) }
    }}

    class Squareup { companion object {
        val Okhttp = Dep.from("com.squareup.okhttp3:okhttp:4.9.3").also { add(it, Squareup) }
        val Retrofit = Dep.from("com.squareup.retrofit2:retrofit:2.9.0").also { add(it, Squareup) }
        val Moshi = Dep.from("com.squareup.moshi:moshi-kotlin:1.13.0").also { add(it, Squareup) }
        val Okio = Dep.from("com.squareup.okio:okio:3.0.0").also { add(it, Squareup) }
        var VERSION_SQLDelight = UNSPECIFIED
        val SQLDelight = Dep.from("com.squareup.sqldelight:gradle-plugin:1.5.3").also { VERSION_SQLDelight = it.version ; add(it, Squareup) }
        val androidDriver = Dep.from("com.squareup.sqldelight:android-driver:$VERSION_SQLDelight")
        val sqliteDriver = Dep.from("com.squareup.sqldelight:sqlite-driver:$VERSION_SQLDelight")
        val nativeDriver = Dep.from("com.squareup.sqldelight:native-driver:$VERSION_SQLDelight")
    }}

    class Misc { companion object {
        val KotlinxDatetime = Dep.from("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1").also {add(it, Misc) }
        val Clikt = Dep.from("com.github.ajalt.clikt:clikt:3.3.0").also { add(it, Misc) }
        val Koodies = Dep.from("com.bkahlert.koodies:koodies:1.9.7").also { add(it, Misc) }
    }}
    class Serialization { companion object {
        val KotlinxJson = Dep.from("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1").also { add(it, Serialization) }
        val KotlinxYaml = Dep.from("net.mamoe.yamlkt:yamlkt:0.10.2").also { add(it, Serialization) }
        val KotlinxKaml = Dep.from("com.charleskorn.kaml:kaml:0.38.0").also { add(it, Serialization) }
        val Snakeyaml = Dep.from("org.yaml:snakeyaml:1.30").also { add(it, Serialization) }
    }}

    class Android { companion object {
        val Tools = Dep.from("com.android.tools.build:gradle:7.0.0", GOOGLE).also { add(it, Android) }
    }}

    class AndroidX { companion object {
        val AppCompatUrl = java.net.URI("https://maven.google.com/web/index.html?q=androidx.appco#androidx.appcompat:appcompat")
        val AppCompat = Dep.from("androidx.appcompat:appcompat:1.4.0").also { add(it, AndroidX) }
    }}

    class ArkIvanov { companion object {
        var VERSION_MVIKotlin = UNSPECIFIED
        val MVIKotlin = Dep.from("com.arkivanov.mvikotlin:mvikotlin:2.0.4").also { VERSION_MVIKotlin = it.version ; add(it, ArkIvanov) }
        val MVIKotlinRX = Dep.from("com.arkivanov.mvikotlin:rx:$VERSION_MVIKotlin")
        val MVIkotlinMain = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main:$VERSION_MVIKotlin")
        val MVIkotlinMainIosX64 = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main-iosx64:$VERSION_MVIKotlin")
        val MVIkotlinMainIosArm64 = Dep.from("com.arkivanov.mvikotlin:mvikotlin-main-iosarm64:$VERSION_MVIKotlin")
        val MVIkotlinLogging = Dep.from("com.arkivanov.mvikotlin:mvikotlin-logging:$VERSION_MVIKotlin")
        val MVIkotlinTimeTravel = Dep.from("com.arkivanov.mvikotlin:mvikotlin-timetravel:$VERSION_MVIKotlin")
        val MVIkotlinExtensionsReaktive = Dep.from("com.arkivanov.mvikotlin:mvikotlin-extensions-reaktive:$VERSION_MVIKotlin")

        var VERSION_Decompose = UNSPECIFIED
        val Decompose = Dep.from("com.arkivanov.decompose:decompose:0.4.0").also { VERSION_Decompose = it.version ; add(it, ArkIvanov) }
        val DecomposeIosX64 = Dep.from("com.arkivanov.decompose:decompose-iosx64:$VERSION_Decompose")
        val DecomposeIosArm64 = Dep.from("com.arkivanov.decompose:decompose-iosarm64:$VERSION_Decompose")
        val extensionsCompose = Dep.from("com.arkivanov.decompose:extensions-compose-jetbrains:$VERSION_Decompose")
    }}

    class Badoo { companion object {
        var VERSION_Badoo = UNSPECIFIED
        val Reaktive = Dep.from("com.badoo.reaktive:reaktive:1.2.1").also { VERSION_Badoo = it.version ; add(it, Badoo) }
        val ReaktiveTesting = Dep.from("com.badoo.reaktive:reaktive-testing:$VERSION_Badoo")
        val utils = Dep.from("com.badoo.reaktive:utils:$VERSION_Badoo")
        val coroutinesInterop = Dep.from("com.badoo.reaktive:coroutines-interop:$VERSION_Badoo")
    }}
}

data class Dep(
    val group: String,
    val artifact: String,
    var version: String = UNSPECIFIED,
    val repo: Repo = MAVENCENTRAL,
    val versionRegex: String? = null
) {
    constructor(groupAndName: String, version: String = UNSPECIFIED, repo: Repo = MAVENCENTRAL, versionRegex: String? = null):
            this(groupAndName.split(':')[0], groupAndName.split(':')[1], version, repo, versionRegex)
    override fun toString(): String = groupAndArtifact() + if(version == UNSPECIFIED) "" else ":${version}"
    fun full(): String = "$group:$artifact:$version"
    fun groupAndArtifact(): String = "$group:$artifact"
    fun toDirPath(): String = "${group.replace('.', '/')}/$artifact"
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

data class DepPlugin(val name: String, val id: String, val VERSION: String) {
    fun add() { Deps.APPLIED_PLUGINS.add(this) }
}

