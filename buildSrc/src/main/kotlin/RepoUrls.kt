import java.net.URL

sealed class Repo(open val baseURL: String, open val interactiveUrl: String = baseURL) {
    abstract fun mavenMetadataXmlURL(dep: Dep): URL
    abstract fun interactiveUrl(dep: Dep): URL
}
object MAVENCENTRAL : Repo("https://repo.maven.apache.org/maven2") {
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}/${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
object JCENTER : Repo("https://bintray.com/bintray/jcenter/download_file?file_path=") {
    //override val baseURL: String = super.baseURL
    //override val interactiveUrl: String = super.interactiveUrl
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
object GOOGLE : Repo("https://dl.google.com/android/maven2/", "https://maven.google.com/web/index.html") {
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
object JETBRAINS : Repo("https://maven.pkg.jetbrains.space/public/p/compose/dev/") {
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
object JITPACK : Repo("https://jitpack.io/") {
    override fun mavenMetadataXmlURL(dep: Dep): URL = URL("${baseURL}${dep.toDirPath()}/maven-metadata.xml")
    override fun interactiveUrl(dep: Dep): URL {
        TODO("Not yet implemented")
    }
}
