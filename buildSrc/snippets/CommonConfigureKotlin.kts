fun Project.buildSrcCommonConfigureKotlin() {
    val prj = this
    prj.plugins.withId("org.jetbrains.kotlin.multiplatform") {
        val kotlinMultiplatformExtension = prj.extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java)
        kotlinMultiplatformExtension?.apply {
            // THIS IS THE ACTUAL kotlin { ... } configure in case of a kotlin("multiplatform") project
            afterEvaluate {
                println("project ${prj.name}: configure from configureKotlinProjectFromRootSubprojectClause() for kotlin MPP project ...")
                jvmToolchain(BuildSrcGlobal.jdkVersion)
            }
            // END OF kotlin { ... } configure in case of a kotlin("multiplatform") project
            val build by prj.tasks.existing
            build.configure { doLast {
                val kotlinTopLevelExtension = kotlinMultiplatformExtension as org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension
                print  ("   Kotlin version: " + kotlinTopLevelExtension.coreLibrariesVersion) ; if (kotlinTopLevelExtension.coreLibrariesVersion != BuildSrcGlobal.VersionKotlin) println(" ( != ${BuildSrcGlobal.VersionKotlin} )") else println()
                println("   java.sourceCompatibility: ${java.sourceCompatibility}")
                println("   java.targetCompatibility: ${java.targetCompatibility}")
                println("   gradle version: ${gradle.gradleVersion}")
                val mppCompileConfiguration = prj.configurations.first { it.name.endsWith("CompileClasspath") && it.isCanBeResolved }
                val regex = Regex(pattern = "^(spring-cloud-starter|spring-boot-starter|micronaut-core|kotlin-stdlib-[0-9]|foundation-desktop).*$")
                val jarsToReport = mppCompileConfiguration.filter { it.name.matches(regex) }.files
                if (jarsToReport.isNotEmpty()) {
                    println("    chosen first '...CompileClasspath configuration to inspect JVM classpath: ${mppCompileConfiguration.name}")
                    jarsToReport.forEach { println("    ${project.name}: ${it.name}") }
                }
            } }
        }
    }
    project.plugins.withId("org.jetbrains.kotlin.jvm") {
        project.extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension::class.java)?.apply {
            // THIS IS THE ACTUAL kotlin { ... } configure in case of a kotlin("jvm") project
            afterEvaluate {
                println("project ${project.name}: configure from configureKotlinProjectFromRootSubprojectClause() for kotlin JVM project ...")
                jvmToolchain(BuildSrcGlobal.jdkVersion)
            }
            // END OF kotlin { ... } configure in case of a kotlin("jvm") project
            val build by project.tasks.existing
            build.configure { doLast {
                println("project ${project.name}: versionReport from configureKotlinProjectFromRootSubprojectClause() for kotlin JVM project ...")
                print  ("   Kotlin version: " + kotlin.coreLibrariesVersion) ; if (kotlin.coreLibrariesVersion != BuildSrcGlobal.VersionKotlin) println(" ( != ${BuildSrcGlobal.VersionKotlin} )") else println()
                println("   java.sourceCompatibility: ${java.sourceCompatibility}")
                println("   java.targetCompatibility: ${java.targetCompatibility}")
                println("   gradle version: ${project.gradle.gradleVersion}")
                val regex = Regex(pattern = "^(spring-cloud-starter|spring-boot-starter|micronaut-core|kotlin-stdlib-[0-9]|foundation-desktop).*$")
                prj.configurations.compileClasspath.get().files.filter { it.name.matches(regex) }
                    .forEach { println("    ${project.name}: ${it.name}") }
            } }
        }
    }
}
