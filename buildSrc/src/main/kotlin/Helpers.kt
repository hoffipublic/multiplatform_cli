import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.DependencyHandlerScope
import java.io.File
import java.util.concurrent.TimeUnit

fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60L,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String? = ProcessBuilder("\\s".toRegex().split(this))
    .directory(workingDir)
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .redirectError(ProcessBuilder.Redirect.PIPE)
    .start().apply { waitFor(timeoutAmount, timeoutUnit) }
    .inputStream.bufferedReader().readText()

private val dependencyHandlerScopes = mutableMapOf<String, DependencyHandlerScope.() -> Unit>()
fun defineDependecies(name: String, dependencyHandlerScope: DependencyHandlerScope.() -> Unit) {
    dependencyHandlerScopes[name] = dependencyHandlerScope
}
fun DependencyHandlerScope.invokeDependencies(name: String) {
    if ( ! dependencyHandlerScopes.containsKey(name)) { throw GradleException("deps: \"$name\" undefined! (known names: ${dependencyHandlerScopes.keys.joinToString("\", \"", "\"", "\"")})") }
    dependencyHandlerScopes[name]!!.invoke(this)
}
