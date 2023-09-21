plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id("com.google.gms.google-services").version("4.3.15").apply(false)
}

buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath("dev.icerock.moko:resources-generator:0.23.0")
        classpath("com.google.gms:google-services:4.3.5")
    }
}


