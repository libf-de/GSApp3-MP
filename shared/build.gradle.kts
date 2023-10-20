/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023 Fabian Schillig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
    id("dev.icerock.mobile.multiplatform-resources")
    id("app.cash.sqldelight")
}

kotlin {
    @Suppress("OPT_IN_USAGE")
    targetHierarchy.default()

    androidTarget()

    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.withType<org.jetbrains.kotlin.gradle.plugin.mpp.Framework> {
            linkerOpts.add("-lsqlite3")
        }
    }

    cocoapods {
        version = "1.0.0"
        summary = "GSApp Multiplatform cocoapod pod"
        homepage = "https://libf.de/gsapp"
        ios.deploymentTarget = "14.1" // TODO: Can I go lower?
        podfile = project.file("../iosApp/Podfile")
        pod("sqlite3")
        pod("HTMLKit") // Used for html parsing on iOS
        //pod("FirebaseMessaging") // Used (in the future) for push notifications on iOS
        framework {
            baseName = "shared"
            isStatic = true

            linkerOpts.add("-lsqlite3")  // TODO: Find out which linker flags are actually needed

            // Used to provide (localized) resources on iOS
            export("dev.icerock.moko:resources:0.23.0")
            export("dev.icerock.moko:graphics:0.9.0") // toUIColor here
        }

        //extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**']"
    }

    sourceSets {
        val ktorVersion = "2.3.4"
        val precomposeVersion = "1.5.1"
        val loggingVersion = "1.3.0"

        val commonMain by getting {
            dependencies {
                // Common Compose Multiplatform dependencies
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.animation)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources) //TODO: Is this needed?

                // Logging
                api("org.lighthousegames:logging:$loggingVersion")

                // Support for Kotlin Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                // Support for immutable Collections (should improve recomposing performance) TODO: Does it?
                implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")

                // Kodein Dependency Injection +Compose Support
                implementation("org.kodein.di:kodein-di-framework-compose:7.19.0")

                // KStore (kv-storage using json files) -> used in JsonDataSource TODO: Remove
                implementation("io.github.xxfast:kstore:0.6.0")
                implementation("io.github.xxfast:kstore-file:0.6.0")

                // Kotlinx Datetime -> Crossplatform DateTime implementation
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

                // Ktor -> used to do web requests
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")

                // Multiplatform Settings
                implementation("com.russhwolf:multiplatform-settings:1.1.0")
                implementation("com.russhwolf:multiplatform-settings-coroutines:1.1.0")


                // Sqldelight coroutines extension
                implementation("app.cash.sqldelight:coroutines-extensions:2.0.0")

                // Insetsx -> Provides paddings respecting on-screen keyboards
                //implementation("com.moriatsushi.insetsx:insetsx:0.1.0-alpha10")

                // Precompose -> Multiplatform Navigation
                api("moe.tlaster:precompose:$precomposeVersion")
                api("moe.tlaster:precompose-viewmodel:$precomposeVersion")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                // Compose Multiplatform
                api("androidx.activity:activity-compose:1.7.2")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.10.1")

                // Skrapeit -> used for html parsing
                implementation("it.skrape:skrapeit:1.2.2")

                // Android Database Driver
                implementation("app.cash.sqldelight:android-driver:2.0.0")

                // Firebase Messaging -> used for push notification
                implementation("com.google.firebase:firebase-messaging-ktx:23.2.1")

                // Android implementation of FlowSettings
                implementation("com.russhwolf:multiplatform-settings-datastore:1.1.0")
                implementation("androidx.datastore:datastore-preferences:1.0.0")


                // Preview android composables
                implementation(compose.preview)
            }
        }
        val iosMain by getting {
            dependencies {
                dependsOn(commonMain)

                // Sqldelight iOS database driver
                implementation("app.cash.sqldelight:native-driver:2.0.0")

                // Ktor iOS driver TODO: Needed?
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
        val desktopMain by getting {
            dependencies {
                dependsOn(commonMain)

                //Compose Multiplatform dependencies
                implementation(compose.desktop.common)
                implementation(compose.desktop.macos_x64)
                implementation(compose.preview)

                // Sqldelight desktop database driver
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0")

                // Used to get cache folder on desktop OSes TODO: Remove, only used in JsonDataSource
                implementation("net.harawata:appdirs:1.2.2")

                // Skrapeit, used for html parsing
                implementation("it.skrape:skrapeit:1.2.2")

                // Kotlinx coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "de.xorg.gsapp"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    commonMainApi("dev.icerock.moko:resources:0.23.0") // TODO: Move to commonMain
    commonMainApi("dev.icerock.moko:resources-compose:0.23.0") // for compose multiplatform

    commonTestImplementation("dev.icerock.moko:resources-test:0.23.0") // for testing
}


multiplatformResources {
    multiplatformResourcesPackage = "de.xorg.gsapp.res"
    disableStaticFrameworkWarning = true
}

sqldelight {
    databases {
        create("GsAppDatabase") {
            packageName.set("de.xorg.gsapp.data.sql")
        }
    }
    linkSqlite = true
}