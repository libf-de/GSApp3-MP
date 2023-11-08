/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023. Fabian Schillig
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
    id(Deps.Moko.Resources._plugin)
    id(Deps.Sqldelight._plugin)
    id(Deps.SonarQube._plugin)
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
        version = BuildConfig.VERSION
        summary = BuildConfig.APP_NAME
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
            export(Deps.Moko.Resources.Core)
            export(Deps.Moko.Graphics) // toUIColor here
        }

        //extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**']"
    }

    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(Deps.Moko.Resources.Test)
            }
        }

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
                api(Deps.Logging)

                // Support for Kotlin Coroutines
                implementation(Deps.KotlinX.Coroutines)

                // Support for immutable Collections (should improve recomposing performance) TODO: Does it?
                implementation(Deps.KotlinX.ImmutableCollections)

                // Koin Dependency Injection
                api(Deps.Koin.Core)
                implementation(Deps.Koin.Compose)

                // Kotlinx Datetime -> Crossplatform DateTime implementation
                implementation(Deps.KotlinX.Datetime)

                // Ktor -> used to do web requests
                implementation(Deps.Ktor.Core)
                implementation(Deps.Ktor.Cio)

                // Multiplatform Settings
                implementation(Deps.MultiplatformSettings.Core)
                implementation(Deps.MultiplatformSettings.Coroutines)

                // Multiplatform Resources (moko resources)
                api(Deps.Moko.Resources.Core)
                api(Deps.Moko.Resources.Compose)

                // Sqldelight coroutines extension
                implementation(Deps.Sqldelight.Coroutines)

                // Window size classes
                implementation(Deps.WindowSizeClass)

                // Insetsx -> Provides paddings respecting on-screen keyboards
                //TODO: Does stuff work without it?
                //implementation("com.moriatsushi.insetsx:insetsx:0.1.0-alpha10")

                // Precompose -> Multiplatform Navigation
                api(Deps.Precompose.Core)
                api(Deps.Precompose.ViewModel)
                api(Deps.Precompose.Koin)

            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                // Compose Multiplatform
                api(Deps.AndroidX.ActivityCompose)
                api(Deps.AndroidX.AppCompat)
                api(Deps.AndroidX.CoreKtx)

                // Skrapeit -> used for html parsing
                implementation(Deps.SkrapeIt)

                // Android Database Driver
                implementation(Deps.Sqldelight.AndroidDriver)

                // Firebase Messaging -> used for push notification
                implementation(Deps.FirebaseMessaging)

                // Android implementation of FlowSettings
                implementation(Deps.MultiplatformSettings.Datastore)
                implementation(Deps.AndroidX.DatastorePreferences)

                // Preview android composables
                implementation(compose.preview)
            }
        }
        val iosMain by getting {
            dependencies {
                dependsOn(commonMain)

                // Sqldelight iOS database driver
                implementation(Deps.Sqldelight.NativeDriver)

                // Ktor iOS driver TODO: Needed?
                implementation(Deps.Ktor.Darwin)
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
                implementation(Deps.Sqldelight.SqliteDriver)

                // Skrapeit, used for html parsing
                implementation(Deps.SkrapeIt)

                // Kotlinx coroutines
                implementation(Deps.KotlinX.CoroutinesSwing)
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
        kotlinCompilerExtensionVersion = Versions.Kotlin.CompilerExtension
    }
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
    linkSqlite.set(true)
}