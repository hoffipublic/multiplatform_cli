import org.gradle.api.GradleException
import java.net.URL

const val UNSPECIFIED = "UNSPECIFIED"
const val GROUP_APACHE = "APACHE"
const val GROUP_CONSOLE = "CONSOLE"
const val GROUP_CORE = "CORE"
const val GROUP_DB = "DB"
const val GROUP_JETBRAINS = "JETBRAINS"
const val GROUP_LOGGING = "LOGGING"
const val GROUP_MPP = "MULTIPLATFORM"
const val GROUP_SERIALIZATION = "SERIALIZATION"
const val GROUP_SQUAREUP = "SQUAREUP"
const val GROUP_TESTING = "TESTING"
const val GROUP_WEB = "WEB"

fun String.depAndVersion() = DepVersions.v(this)
fun String.depButVersionOf(otherDep: String) = DepVersions.versionOf(this, otherDep)
fun String.depVersionOnly() = DepVersions.versionOnly(this)
object DepVersions {
    val USED = sortedMapOf<String, MutableSet<Dep>>().toMutableMap()
    val vMap = hashMapOf<String, Dep>(
        Dep.from("org.antlr:antlr4:4.11.1", GROUP_APACHE),
        Dep.from("org.apache.poi:poi:5.2.3", GROUP_APACHE),
        Dep.from("org.apache.poi:poi-ooxml:5.2.3", GROUP_APACHE),

        Dep.from("io.arrow-kt:arrow-core:1.1.5", GROUP_CORE),
        Dep.from("org.kodein.di:kodein-di:7.18.0", GROUP_CORE),

        Dep.from("com.bkahlert.koodies:koodies:1.9.7", GROUP_CONSOLE),
        Dep.from("com.github.ajalt.clikt:clikt:3.5.1", GROUP_CONSOLE),

        Dep.from("com.h2database:h2:2.1.214", GROUP_DB),
        //Dep.from("org.postgresql:postgresql:42.5.4", GROUP_DB, versionRegex = "${THREEDIGITSs}\\.jre\\d*\$"),
        Dep.from("org.postgresql:postgresql:42.5.4", GROUP_DB),

        // __LATEST_COMPOSE_RELEASE_VERSION__ https://github.com/JetBrains/compose-jb/releases
        Dep.from("org.jetbrains.compose:compose-gradle-plugin:1.3.0", GROUP_JETBRAINS, repo = JETBRAINSREPO),
        Dep.from("org.jetbrains.exposed:exposed-core:0.41.1", GROUP_JETBRAINS),
        Dep.from("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4", GROUP_JETBRAINS),
        Dep.from("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0", GROUP_JETBRAINS),
        Dep.from("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC", GROUP_JETBRAINS),

        Dep.from("ch.qos.logback:logback-classic:1.4.5", GROUP_LOGGING),
        Dep.from("io.github.microutils:kotlin-logging:3.0.5", GROUP_LOGGING),
        Dep.from("org.slf4j:slf4j-api:2.0.6", GROUP_LOGGING),

        Dep.from("com.benasher44:uuid:0.6.0", GROUP_MPP),

        Dep.from("com.charleskorn.kaml:kaml:0.51.0", GROUP_SERIALIZATION),
        Dep.from("net.mamoe.yamlkt:yamlkt:0.12.0", GROUP_SERIALIZATION),
        Dep.from("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0-RC", GROUP_SERIALIZATION),
        Dep.from("org.yaml:snakeyaml:1.33", GROUP_SERIALIZATION),

        Dep.from("com.squareup:kotlinpoet:1.12.0", GROUP_SQUAREUP),
        Dep.from("com.squareup.okhttp3:okhttp:4.10.0", GROUP_SQUAREUP),
        Dep.from("com.squareup.moshi:moshi-kotlin:1.14.0", GROUP_SQUAREUP),
        Dep.from("com.squareup.okio:okio:3.3.0", GROUP_SQUAREUP),
        Dep.from("com.squareup.retrofit2:retrofit:2.9.0", GROUP_SQUAREUP),
        Dep.from("com.squareup.sqldelight:gradle-plugin:1.5.5", GROUP_SQUAREUP),
        Dep.from("com.squareup.sqldelight:android-driver:1.5.5", GROUP_SQUAREUP),
        Dep.from("com.squareup.sqldelight:native-driver:1.5.5", GROUP_SQUAREUP),
        Dep.from("com.squareup.sqldelight:sqlite-driver:1.5.5", GROUP_SQUAREUP),

        Dep.from("org.junit.jupiter:junit-jupiter-api:5.9.2", GROUP_TESTING),
        Dep.from("org.junit.jupiter:junit-jupiter-engine:5.9.2", GROUP_TESTING),
        Dep.from("org.jetbrains.kotlin:kotlin-test-common:${BuildSrcGlobal.VersionKotlin}"),
        Dep.from("org.jetbrains.kotlin:kotlin-test-junit:${BuildSrcGlobal.VersionKotlin}"),
        Dep.from("org.jetbrains.kotlin:kotlin-test-annotations-common:${BuildSrcGlobal.VersionKotlin}"),
        Dep.from("io.kotest:kotest-runner-junit5:5.5.5", GROUP_TESTING),
        Dep.from("org.hamcrest:hamcrest-library:2.2", GROUP_TESTING),

        Dep.from("com.rabbitmq:amqp-client:5.16.0", GROUP_WEB),
        Dep.from("io.github.hakky54:sslcontext-kickstart:7.4.9", GROUP_WEB),
        Dep.from("io.github.resilience4j:resilience4j-core:2.0.2", GROUP_WEB),
        Dep.from(  "io.ktor:ktor-server-core:2.2.3", GROUP_WEB),
        Dep.from(  "io.ktor:ktor-client-core:2.2.3", GROUP_WEB),
        Dep.from("org.jsoup:jsoup:1.15.3", GROUP_WEB),
    )
    fun v(groupAndArtifact: String): String {
        val dep = vMap[groupAndArtifact] ?: throw GradleException("unknown dependency version (not in 'buildSrc/src/main/kotlin/DepVersions.kt'): \"$groupAndArtifact\"")
        USED.getOrPut(dep.groupkey){emptySet<Dep>().toMutableSet()}.add(dep)
        return "$groupAndArtifact:${dep.version}"
    }
    fun versionOf(groupAndArtifact: String, otherDep: String): String {
        val theOtherDep = vMap[otherDep] ?: throw GradleException("unknown dependency version (not in 'buildSrc/src/main/kotlin/DepVersions.kt'): \"$otherDep\"")
        USED.getOrPut(theOtherDep.groupkey){emptySet<Dep>().toMutableSet()}.add(theOtherDep)
        return "$groupAndArtifact:${theOtherDep.version}"
    }
    fun versionOnly(groupAndArtifact: String): String {
        val dep = vMap[groupAndArtifact] ?: throw GradleException("unknown dependency version (not in 'buildSrc/src/main/kotlin/DepVersions.kt'): \"$groupAndArtifact\"")
        USED.getOrPut(dep.groupkey){emptySet<Dep>().toMutableSet()}.add(dep)
        return dep.version
    }
}

fun String.pluginVersion() = DepVersionPlugins.v(this)
object DepVersionPlugins {
    val USED = mutableSetOf<DepPlugin>()
    val vSet = setOf<DepPlugin>(
        DepPlugin("micronaut", "io.micronaut.application", version = "3.7.2"),
        DepPlugin("shadow", "com.github.johnrengelman.shadow", version = "7.1.2"),
    )
    fun v(pluginName: String): String {
        val pluginDep = vSet.firstOrNull { it.name == pluginName } ?: throw GradleException("unknown plugin (not in 'buildSrc/src/main/kotlin/DepVersions.kt'): \"$pluginName\" not in (${DepVersionPlugins.vSet.map { it.name }.joinToString()})")
        USED.add(pluginDep)
        return pluginDep.version
    }
}
class Dep(
    var groupkey: String = UNSPECIFIED,
    val group: String,
    val artifact: String,
    val version: String = UNSPECIFIED,
    val repo: Repo = MAVENCENTRALREPO,
    val versionMatchRegex: String? = null
) {
    fun toDirPath(): String = "${group.replace('.', '/')}/$artifact"
    fun mavenMetadataXmlURL(): URL = repo.mavenMetadataXmlURL(this)
    fun checkVersion(remoteVersion: String?): Boolean {
        if (remoteVersion == null) return false
        return versionMatchRegex?.toRegex()?.matches(remoteVersion) ?: (remoteVersion == version)
    }
    companion object {
        fun from(depString: String, groupkey: String = UNSPECIFIED, repo: Repo = MAVENCENTRALREPO, versionMatchRegex: String? = null): Pair<String, Dep> {
            val parts = depString.split(':')
            if (parts.size != 3) throw Exception("gradle dependency syntax error on '${depString}'")
            return "${parts[0]}:${parts[1]}" to Dep(groupkey, parts[0], parts[1], parts[2], repo, versionMatchRegex)
        }
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Dep) return false

        if (group != other.group) return false
        if (artifact != other.artifact) return false
        if (version != other.version) return false

        return true
    }
    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + artifact.hashCode()
        result = 31 * result + version.hashCode()
        return result
    }
}

class DepPlugin(val name: String, val id: String, val version: String) {
}

sealed class Repo(open val baseURL: String, open val interactiveUrl: String = baseURL) {
    abstract fun mavenMetadataXmlURL(dep: Dep): URL
    abstract fun interactiveUrl(dep: Dep): URL
}
object MAVENCENTRALREPO : Repo("https://repo.maven.apache.org/maven2") {
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}/${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
object JCENTERREPO : Repo("https://bintray.com/bintray/jcenter/download_file?file_path=") {
    //override val baseURL: String = super.baseURL
    //override val interactiveUrl: String = super.interactiveUrl
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
object GOOGLEREPO : Repo("https://dl.google.com/android/maven2/", "https://maven.google.com/web/index.html") {
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
object JETBRAINSREPO : Repo("https://maven.pkg.jetbrains.space/public/p/compose/dev/") {
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
object JITPACKREPO : Repo("https://jitpack.io/") {
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
