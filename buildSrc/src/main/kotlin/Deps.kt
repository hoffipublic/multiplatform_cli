import Dep.Companion.THREEDIGITSs
import PluginDeps.ALL
import PluginDeps.USED
import java.net.URL

const val UNSPECIFIED = "UNSPECIFIED"

const val APACHE_GROUP = "APACHE"
const val CONSOLE_GROUP = "CONSOLE"
const val CORE_GROUP = "CORE"
const val DB_GROUP = "DB"
const val LOGGING_GROUP = "LOGGING"
const val MPP_GROUP = "MULTIPLATFORM"
const val JETBRAINS_GROUP = "JETBRAINS"
const val SERIALIZATION_GROUP = "SERIALIZATION"
const val SQUAREUP_GROUP = "SQUAREUP"
const val TESTING_GROUP = "TESTING"
const val WEB_GROUP = "WEB"

object PluginDeps {
    val ALL = mutableSetOf<DepPlugin>()
    val USED = mutableSetOf<DepPlugin>()

    val PluginMicronaut = DepPlugin("Micronaut", "io.micronaut.application", version = "3.6.4").also { it.add() }
    val PluginShadow = DepPlugin("Shadow", "com.github.johnrengelman.shadow", version = "7.1.2").also { it.add() }

}
object Deps {
    val ALL = sortedMapOf<String, MutableSet<Dep>>().toMutableMap()
    val USED = sortedMapOf<String, MutableSet<Dep>>().toMutableMap()

    //val Kotlin = Dep.from("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10").also { VERSION_Kotlin = it.version ; add(it, JetBrains) }
    val Antlr            = Dep.from("org.antlr:antlr4:4.11.1").also { it.add(APACHE_GROUP) }
    val Arrow            = Dep.from("io.arrow-kt:arrow-core:1.1.3").also { it.add(CORE_GROUP) }
    val Clikt            = Dep.from("com.github.ajalt.clikt:clikt:3.5.0").also { it.add(CONSOLE_GROUP) }
    // __LATEST_COMPOSE_RELEASE_VERSION__ https://github.com/JetBrains/compose-jb/releases
    val Compose          = Dep.from("org.jetbrains.compose:compose-gradle-plugin:1.2.1-rc03", repo = JETBRAINSREPO).also { it.add(JETBRAINS_GROUP) }
    val Exposed          = Dep.from("org.jetbrains.exposed:exposed-core:0.40.1").also { it.add(JETBRAINS_GROUP) }
    val H2               = Dep.from("com.h2database:h2:2.1.214").also { it.add(DB_GROUP) }
    val jsoup            = Dep.from("org.jsoup:jsoup:1.15.3").also { it.add(WEB_GROUP) }
    val Kodein           = Dep.from("org.kodein.di:kodein-di:7.15.1").also { it.add(CORE_GROUP) }
    val Koodies          = Dep.from("com.bkahlert.koodies:koodies:1.9.7").also { it.add(CONSOLE_GROUP) }
    val KotlinCoroutines = Dep.from("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    val KotlinLogging    = Dep.from("io.github.microutils:kotlin-logging:3.0.4").also { it.add(LOGGING_GROUP) }
    val KotlinxDatetime  = Dep.from("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0").also { it.add(JETBRAINS_GROUP) }
    val KtorServer       = Dep.from("io.ktor:ktor-server-core:2.1.3").also { it.add(WEB_GROUP) }
    val KtorClient       = Dep.from("io.ktor:ktor-client-core:${KtorServer.versionNonAdding}")
    val KtorClientCIO    = Dep.from("io.ktor:ktor-client-cio:${KtorServer.versionNonAdding}")
    val KtorClientOkio   = Dep.from("io.ktor:ktor-client-okhttp:${KtorServer.versionNonAdding}")
    val KtorClientCN     = Dep.from("io.ktor:ktor-client-content-negotiation:${KtorServer.versionNonAdding}")
    val Logback          = Dep.from("ch.qos.logback:logback-classic:1.4.4").also { it.add(LOGGING_GROUP) }
    val Poi              = Dep.from("org.apache.poi:poi:5.2.3").also { it.add(APACHE_GROUP) }
    val PoiOoxml         = Dep.from("org.apache.poi:poi-ooxml:${Poi.versionNonAdding}")
    //val Postgresql       = Dep.from("org.postgresql:postgresql:42.5.0", versionRegex = "${THREEDIGITSs}\\.jre\\d*\$").also { it.add(DB_GROUP) }
    val Postgresql       = Dep.from("org.postgresql:postgresql:42.5.0").also { it.add(DB_GROUP) }
    val Rabbit           = Dep.from("com.rabbitmq:amqp-client:5.16.0").also { it.add(WEB_GROUP) }
    val Resilience4j     = Dep.from("io.github.resilience4j:resilience4j-core:1.7.1").also { it.add(WEB_GROUP) }
    val Slf4jApi         = Dep.from("org.slf4j:slf4j-api:2.0.3").also { it.add(LOGGING_GROUP) }
    val Sslcontext       = Dep.from("io.github.hakky54:sslcontext-kickstart:7.4.8").also { it.add(WEB_GROUP) }
    val UUID             = Dep.from("com.benasher44:uuid:0.6.0").also { it.add(MPP_GROUP) }

    // SERIALIZATION
    val KotlinxJson      = Dep.from("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1").also { it.add(SERIALIZATION_GROUP) }
    val KotlinxYaml      = Dep.from("net.mamoe.yamlkt:yamlkt:0.12.0").also { it.add(SERIALIZATION_GROUP) }
    val KotlinxKaml      = Dep.from("com.charleskorn.kaml:kaml:0.49.0").also { it.add(SERIALIZATION_GROUP) }
    val Snakeyaml        = Dep.from("org.yaml:snakeyaml:1.33").also { it.add(SERIALIZATION_GROUP) }

    // SQUAREUP
    val kotlinPoet       = Dep.from("com.squareup:kotlinpoet:1.12.0").also { it.add(SQUAREUP_GROUP) }
    val Okhttp           = Dep.from("com.squareup.okhttp3:okhttp:4.10.0").also { it.add(SQUAREUP_GROUP) }
    val Moshi            = Dep.from("com.squareup.moshi:moshi-kotlin:1.14.0").also { it.add(SQUAREUP_GROUP) }
    val Okio             = Dep.from("com.squareup.okio:okio:3.2.0").also { it.add(SQUAREUP_GROUP) }
    val Retrofit         = Dep.from("com.squareup.retrofit2:retrofit:2.9.0").also { it.add(SQUAREUP_GROUP) }
    val SQLDelight       = Dep.from("com.squareup.sqldelight:gradle-plugin:1.5.4").also { it.add(SQUAREUP_GROUP) }
    val androidDriver    = Dep.from("com.squareup.sqldelight:android-driver:${SQLDelight.versionNonAdding}")
    val nativeDriver     = Dep.from("com.squareup.sqldelight:native-driver:${SQLDelight.versionNonAdding}")
    val sqliteDriver     = Dep.from("com.squareup.sqldelight:sqlite-driver:${SQLDelight.versionNonAdding}")

    // TESTING
    val Junit = Dep.from("org.junit.jupiter:junit-jupiter-api:5.9.1").also { it.add(TESTING_GROUP) }
    val JunitEngine = Dep.from("org.junit.jupiter:junit-jupiter-engine:${Junit.versionNonAdding}")
    val KotlinTestCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-common:${BuildSrcGlobal.VersionKotlin}")
    val KotlinTestJunit = Dep.from("org.jetbrains.kotlin:kotlin-test-junit:${BuildSrcGlobal.VersionKotlin}")
    val KotlinTestAnnotationsCommon = Dep.from("org.jetbrains.kotlin:kotlin-test-annotations-common:${BuildSrcGlobal.VersionKotlin}")
    val Kotest = Dep.from("io.kotest:kotest-runner-junit5-jvm:5.5.4").also { it.add(TESTING_GROUP) }
    val hamcrestLibrary = Dep.from("org.hamcrest:hamcrest-library:2.2").also { it.add(TESTING_GROUP) }
}

class Dep(
    var groupkey: String = UNSPECIFIED,
    group: String,
    artifact: String,
    version: String = UNSPECIFIED,
    val repo: Repo = MAVENCENTRALREPO,
    val versionRegex: String? = null
) {
    val group: String = group
        get() { use(groupkey); return field }
    val groupNonAdding = group // for equals and hashCode to break recursion
    val artifact: String = artifact
        get() { use(groupkey); return field }
    val artifactNonAdding = artifact // for equals and hashCode to break recursion
    val version: String = version
        get() { use(groupkey); return field }
    val versionNonAdding = version // for equals and hashCode to break recursion

    constructor(groupAndName: String, version: String = UNSPECIFIED, repo: Repo = MAVENCENTRALREPO, versionRegex: String? = null):
            this(UNSPECIFIED, groupAndName.split(':')[0], groupAndName.split(':')[1], version, repo, versionRegex)

    fun add(key: String): Dep { Deps.ALL.getOrPut(key){emptySet<Dep>().toMutableSet()}.add(this) ; return this }
    fun use(key: String): Dep { Deps.USED.getOrPut(key){emptySet<Dep>().toMutableSet()}.add(this) ; return this }

    override fun toString(): String = groupAndArtifact() + if(versionNonAdding == UNSPECIFIED) "" else ":${versionNonAdding}"
    fun full(): String = "$groupNonAdding:$artifactNonAdding:$versionNonAdding"
    fun groupAndArtifact(): String = "$groupNonAdding:$artifactNonAdding"
    fun toDirPath(): String = "${groupNonAdding.replace('.', '/')}/$artifactNonAdding"
    fun mavenMetadataXmlURL(): URL = repo.mavenMetadataXmlURL(this)
    fun checkVersion(remoteVersion: String?): Boolean {
        if (remoteVersion == null) return false
        return versionRegex?.toRegex()?.matches(remoteVersion) ?: (remoteVersion == versionNonAdding)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Dep) return false

        if (groupNonAdding != other.groupNonAdding) return false
        if (artifactNonAdding != other.artifactNonAdding) return false
        if (versionNonAdding != other.versionNonAdding) return false

        return true
    }

    override fun hashCode(): Int {
        var result = groupNonAdding.hashCode()
        result = 31 * result + artifactNonAdding.hashCode()
        result = 31 * result + versionNonAdding.hashCode()
        return result
    }

    companion object {
        val THREEDIGITSs = "^(\\d+\\.)?(\\d+\\.)?(\\*|\\d+)"
        val THREEDIGITS = Regex("$THREEDIGITSs\$")
        fun from(depString: String, repo: Repo = MAVENCENTRALREPO, versionRegex: String? = null): Dep {
            val parts = depString.split(':')
            if (!(2..3).contains(parts.size)) throw Exception("gradle dependency syntax error on '${depString}'")
            return when (parts.size) {
                3 -> Dep(UNSPECIFIED, parts[0], parts[1], parts[2], repo, versionRegex)
                else -> Dep(UNSPECIFIED, parts[0], parts[1], UNSPECIFIED, repo, versionRegex)
            }
        }
    }

}

class DepPlugin(name: String, id: String, version: String) {
    val name: String = name
        get() { use(); return field }
    val nameNonAdding = name // for equals and hashCode to break recursion
    val id: String = id
        get() { use(); return field }
    val idNonAdding = id // for equals and hashCode to break recursion
    val version: String = version
        get() { use(); return field }
    val versionNonAdding = version // for equals and hashCode to break recursion

    fun add(): DepPlugin { ALL.add(this) ; return this }
    fun use(): DepPlugin { USED.add(this) ; return this }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DepPlugin) return false

        if (nameNonAdding != other.nameNonAdding) return false
        if (idNonAdding != other.idNonAdding) return false
        if (versionNonAdding != other.versionNonAdding) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nameNonAdding.hashCode()
        result = 31 * result + idNonAdding.hashCode()
        result = 31 * result + versionNonAdding.hashCode()
        return result
    }

}

