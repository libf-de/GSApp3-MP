plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.jetbrainsCompose).apply(false)
    alias(libs.plugins.googleServices).apply(false)
    alias(libs.plugins.sqldelight).apply(false)
    alias(libs.plugins.sonarqube)
}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath(libs.moko.resources.generator)
        classpath(libs.google.services)
        classpath(libs.kotlin.gradlePlugin)
    }
}

val buildInfo = tasks.register("createBuildInfoFile") {
    doLast {
        val buildInfoFile = File("$projectDir/composeApp/src/commonMain/kotlin/BuildInfo.kt")
        buildInfoFile.writeText("object BuildInfo {\n" +
                "    const val BUILD_VERSION = \"1.0.1\"\n" +
                "    const val BUILD_NUMBER = 6000\n" +
                "    const val IS_DEBUG = false\n" +
                "}")
    }
}