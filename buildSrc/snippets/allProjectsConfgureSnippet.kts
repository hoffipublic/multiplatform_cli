fun <T: org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension> configureAllKotlinProjects(vararg projectExtensionsClasses: kotlin.reflect.KClass<T>) {
    projectExtensionsClasses.forEach { projectExtensionClass: kotlin.reflect.KClass<T> ->
        project.extensions.findByType(projectExtensionClass.java)?.apply {
            println("subprojects->${project.name}->jvmToolchain(${BuildSrcGlobal.jdkVersion})")
            when (projectExtensionClass) {
                is org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension -> {
                    projectExtensionClass.jvmToolchain(BuildSrcGlobal.jdkVersion)
                }
                is org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension -> {
                    projectExtensionClass.jvmToolchain(BuildSrcGlobal.jdkVersion)
                }
            }
        }
    }
}

subprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    project.plugins.withId("org.jetbrains.kotlin.multiplatform") {
        println("${project.name}: starting configure for kotlin MPP project ...")
        configureAllKotlinProjects(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class)
    }
    project.plugins.withId("org.jetbrains.kotlin.jvm") {
        println("${project.name}: starting configure for kotlin JVM project ...")
        configureAllKotlinProjects(org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension::class)
    }

//    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
//        project.extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java)?.apply {
//            println("subprojects->${project.name}->jvmToolchain(${BuildSrcGlobal.jdkVersion})")
//            jvmToolchain {
//                jvmToolchain(BuildSrcGlobal.jdkVersion)
//            }
//        }
//    }
//    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
//        project.extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension::class.java)?.apply {
//            println("subprojects->${project.name}->jvmToolchain(${BuildSrcGlobal.jdkVersion})")
//            jvmToolchain {
//                jvmToolchain(BuildSrcGlobal.jdkVersion)
//            }
//        }
//    }
//    configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
//        jvmToolchain {
//            jvmToolchain(BuildSrcGlobal.jdkVersion)
//        }
//    }
}
