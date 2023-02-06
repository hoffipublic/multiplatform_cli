tasks.register("printClasspath") {
    group = "misc"
    description = "print classpath"
    doLast {
//        project.getConfigurations().filter { it.isCanBeResolved }.forEach {
//            println(it.name)
//        }
//        println()
        val targets = listOf(
            "jvmRuntimeClasspath",
//            "kotlinCompilerClasspath"
        )
        targets.forEach { targetConfiguration ->
            println("$targetConfiguration:")
            println("=".repeat("$targetConfiguration:".length))
            project.getConfigurations()
                .getByName(targetConfiguration).files
                // filters only existing and non-empty dirs
                .filter { (it.isDirectory() && it.listFiles().isNotEmpty()) || it.isFile() }
                .forEach { println(it) }
        }
    }
}
