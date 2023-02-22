/** create package dirs under each subprojects src/module/kotlin
 * based on subproject's extra property: projectPackage
 *
 * in rootProject:
 * //=============
 * group = "com.hoffi"
 * version = "1.0-SNAPSHOT"
 * val artifactName by extra { "${rootProject.name.toLowerCase()}-${project.name.toLowerCase()}" }
 * val rootPackage by extra { "${rootProject.group}.${rootProject.name.toLowerCase()}" }
 * val theMainClass by extra { "Main" }
 * application {
 *     mainClass.set("${rootPackage}.${theMainClass}" + "Kt") // + "Kt" if fun main is outside a class
 * }
 *
 * in subprojects:
 * //=============
 * group = "${rootProject.group}"
 * version = "${rootProject.version}"
 * val artifactName by extra { "${rootProject.name.toLowerCase()}-${project.name.toLowerCase()}" }
 * val rootPackage: String by rootProject.extra
 * val projectPackage by extra { "${rootPackage}.${project.name.toLowerCase()}" }
 * val theMainClass by extra { "Main" }
 * application {
 *     mainClass.set("${projectPackage}.${theMainClass}" + "Kt") // + "Kt" if fun main is outside a class
 * }
 */
val createSrcBasePackages = tasks.register("createSrcBasePackages") {
    doLast {
        project.subprojects.forEach { prj ->
            var relProjectDirString = prj.projectDir.toString().removePrefix(rootProject.projectDir.toString())
            if (relProjectDirString.isBlank()) { relProjectDirString = "ROOT" } else { relProjectDirString = relProjectDirString.removePrefix("/") }
            println("  in project: $relProjectDirString ...")
            val projectPackage: String by prj.extra
            val projectPackageDirString = projectPackage.split('.').joinToString("/")
            prj.pluginManager.let() { when {
                it.hasPlugin("org.jetbrains.kotlin.jvm") -> {
                    prj.sourceSets.forEach { sourceSet ->
                        val ssDir = File("${prj.projectDir}/src/${sourceSet.name}/kotlin")
                        if (ssDir.exists()) {
                            mkdir("$ssDir/$projectPackageDirString")
                        }
                    }
                }
                it.hasPlugin("org.jetbrains.kotlin.multiplatform") -> {
                    prj.kotlin.sourceSets.forEach { sourceSet ->
                        val ssDir = File("${prj.projectDir}/src/${sourceSet.name}/kotlin")
                        if (ssDir.exists()) {
                            mkdir("$ssDir/$projectPackageDirString")
                        }
                    }
                }
            }}
            println("  in project: $relProjectDirString ok.")
        }
    }
}
