
rootProject.name = "multiplatform_cli"

include(":lib")

include(":cli")

fun includeFromSubSubdir(subdir: String, prjName: String) {
    include(":$prjName")
    project(":$prjName").projectDir = file("$subdir/$prjName")
}
val exesDir = "exes"
val exePrjs = listOf("updir")
for (exePrj in exePrjs) {
    includeFromSubSubdir(exesDir, exePrj)
}
