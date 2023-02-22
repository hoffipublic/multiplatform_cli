/** creating `01__PRJNAME/.gitkeep` and `ZZ__PRJNAME` files in each kotlin mpp project
 * as well as `_srcModule_PRJNAME/.gitkeep` and `ZZsrcModule_PRJNAME` files in each main sourceSets of these
 *
 * .gitignore:
 * <block>
 *  .idea/
 *  !.idea/scopes/
 *  !.idea/fileColors.xml
 * </block>
 *
 * if you had .idea/ ignored before, try
 * <block>
 * git rm --cached .idea/filename
 * git add --forced .idea/filename
 * </block>
 *
 * e.g. define scopes (in Settings... `Scopes`):
 * - scope 00__ (scope with all folders where the name starts with: 0[0-3]__, meaning the first folder
 * - scope src with _src.../ or ZZsrc... (scope with all folders where the name starts with _src)
 * - scope buildfiles (e.g. build.gradle.kts)
 *
 * and then in Settings ... `File Colors` add the scope(s) and give them a color .
 *
 * If you _then_ add folders / files matching the above scope names
 * you can see more clearly which "area" of code in the folder structure you are just looking at the moment .
 */
val createIntellijScopeSentinels = tasks.register("createIntellijScopeSentinels") {
    doLast {
        project.allprojects.forEach { prj ->
            var relProjectDirString = prj.projectDir.toString().removePrefix(rootProject.projectDir.toString())
            if (relProjectDirString.isBlank()) { relProjectDirString = "ROOT" } else { relProjectDirString = relProjectDirString.removePrefix("/") }
            println("  in project: $relProjectDirString ...")
            val suffix = if (prj.name == rootProject.name) {
                "ROOT"
            } else {
                prj.name.toUpperCase()
            }
            prj.pluginManager.let { when {
                it.hasPlugin("org.jetbrains.kotlin.jvm") -> {
                    if (prj.name != rootProject.name) {
                        val dir = mkdir("${prj.projectDir}/01__$suffix")
                        File(dir, ".gitkeep").createNewFile()
                        File(prj.projectDir, "ZZ__$suffix").createNewFile()
                    }
                    prj.sourceSets.forEach { ss: SourceSet ->
                        val ssDir = if (prj.name == rootProject.name) {
                            File("src/${ss.name}")
                        } else {
                            File("${prj.projectDir}/src/${ss.name}")
                        }
                        if (ssDir.exists()) {
                            val mName = ss.name.capitalize()
                            val dir = mkdir("$ssDir/_src${mName}_$suffix")
                            File(dir, ".gitkeep").createNewFile()
                            File(ssDir, "ZZsrc${mName}_$suffix").createNewFile()
                        }
                    }
                }
                it.hasPlugin("org.jetbrains.kotlin.multiplatform") -> {
                    if (prj.name != rootProject.name) {
                        val dir = mkdir("${prj.projectDir}/01__$suffix")
                        File(dir, ".gitkeep").createNewFile()
                        File(prj.projectDir, "ZZ__$suffix").createNewFile()
                    }
                    prj.kotlin.sourceSets.forEach { sourceSet ->
                        val ssDir = if (prj.name == rootProject.name) {
                            File("src/${sourceSet.name}")
                        } else {
                            File("${prj.projectDir}/src/${sourceSet.name}")
                        }
                        if (ssDir.exists()) {
                            if (sourceSet.name.endsWith("Main")) {
                                val mName = sourceSet.name.removeSuffix("Main").capitalize()
                                val dir = mkdir("$ssDir/_src${mName}_$suffix")
                                File(dir, ".gitkeep").createNewFile()
                                File(ssDir, "ZZsrc${mName}_$suffix").createNewFile()
                            }
                        }
                    }
                }
            }}
            println("  in project: $relProjectDirString ok.")
        }
    }
}
