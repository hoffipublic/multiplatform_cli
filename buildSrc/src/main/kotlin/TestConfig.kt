import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import org.gradle.kotlin.dsl.KotlinClosure1
import org.gradle.kotlin.dsl.KotlinClosure2

/** use with:
 * tasks {
 *     withType<Test> {
 *         buildSrcTestConfig()
 *     }
 * }
 */
fun Test.buildSrcJvmTestConfig() {
    // classpath += developmentOnly
    useJUnitPlatform {
        //includeEngines("junit-jupiter", "spek2")
        // includeTags "fast"
        // excludeTags "app", "integration", "messaging", "slow", "trivial"
    }
    failFast = false
    buildSrcCommonTestConfig("JVM")
}
////tasks { withType(org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest::class) { buildSrcNativeTestConfig() } }
//fun org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest.buildSrcNativeTestConfig() {
//    buildSrcCommonTestConfig("NATIVE")
//}

fun AbstractTestTask.buildSrcCommonTestConfig(targetPlatform: String) {
    ignoreFailures = false
    testLogging {
        showStandardStreams = true
        showCauses = false
        showExceptions = false
        showStackTraces = false
        //exceptionFormat = TestExceptionFormat.FULL
        // better logging in beforeXXX, afterXXX below
        //events(
        //    org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
        //    org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
        //    org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
        //) //, STARTED //, standardOut, standardError)
    }

    val ansiReset = "\u001B[0m"
    val ansiGreen = "\u001B[32m"
    val ansiRed = "\u001B[31m"
    val ansiYellow = "\u001B[33m"

    fun getColoredResultType(resultType: TestResult.ResultType): String {
        return when (resultType) {
            TestResult.ResultType.SUCCESS -> "$ansiGreen $resultType $ansiReset"
            TestResult.ResultType.FAILURE -> "$ansiRed $resultType $ansiReset"
            TestResult.ResultType.SKIPPED -> "$ansiYellow $resultType $ansiReset"
        }
    }

    val variant1 = false
    if (variant1) {
        beforeSuite(KotlinClosure1<TestDescriptor, Unit>({
            if (this.className != null) { // if (this.parent == null) // will match the outermost suite
                println()
                //println(this.className?.substringAfterLast(".").orEmpty())
                println("${this.className?.substringAfterLast(".").orEmpty()} (${this.className})")
            }
        }))
    }
    afterTest(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
        if (variant1) {
            println("${desc.displayName} = ${getColoredResultType(result.resultType)}")
        } else {
            println("${desc.className} | ${desc.displayName} = ${getColoredResultType(result.resultType)}")
        }
    }))
    if (variant1) {
        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent == null) { // will match the outermost suite
                println("$targetPlatform Result: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)")
            }
        }))
    } else {
        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent == null) { // will match the outermost suite
                println("\nTotal Test Results:")
                println("===================")
                val failsDefault = "${result.failedTestCount} failures"
                val fails =
                    if (result.failedTestCount > 0) BuildSrcGlobal.colorString(
                        BuildSrcGlobal.ConsoleColor.RED,
                        failsDefault
                    ) else failsDefault
                val outcome = if (result.resultType.name == "FAILURE") BuildSrcGlobal.colorString(
                    BuildSrcGlobal.ConsoleColor.RED,
                    result.resultType.name
                ) else BuildSrcGlobal.colorString(BuildSrcGlobal.ConsoleColor.GREEN, result.resultType.name)
                println("$targetPlatform Test Results: $outcome (total: ${result.testCount} tests, ${result.successfulTestCount} successes, $fails, ${result.skippedTestCount} skipped)\n")
            }
        }))
    }

    // listen to standard out and standard error of the test JVM(s)
    // onOutput { descriptor, event -> logger.lifecycle("Test: " + descriptor + " produced standard out/err: " + event.message ) }
}
