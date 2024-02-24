
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id(libs.plugins.kotlinCocoapods.get().pluginId)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.googleServices)
    /*alias(libs.plugins.sqldelight)*/
    alias(libs.plugins.sonarqube)
}

kotlin {
    kotlin.applyDefaultHierarchyTemplate()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    cocoapods {
        version = libs.versions.gsapp.versionName.get()
        summary = "GSApp"
        homepage = "https://libf.de/gsapp"
        ios.deploymentTarget = "14.1" // TODO: Can I go lower?
        podfile = project.file("../iosApp/Podfile")
        pod("sqlite3")
        pod("HTMLKit") // Used for html parsing on iOS
        //pod("FirebaseMessaging") // Used (in the future) for push notifications on iOS
        framework {
            baseName = "GSApp"
            isStatic = true

            linkerOpts.add("-lsqlite3")  // TODO: Find out which linker flags are actually needed
        }

        //extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
        //extraSpecAttributes["resources"] = "['src/commonMain/resources/**']"
    }
    
    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }

        commonMain.dependencies {
            // The sqldelight gradle plugin automatically adds it's runtime to commonMain, but it
            // currently doesn't support wasm. Therefore it is excluded here, and is manually added
            // to nonWebMain
            /*configurations.first {
                it.name.contains("commonMain")
            }.exclude(
                group = "app.cash.sqldelight",
                module = "runtime"
            )*/

            // Compose dependencies
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.animation)
            implementation(compose.material3)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            // Logging
            api(libs.logging)

            // Support for Kotlin Coroutines
            implementation(libs.kotlinx.coroutines)

            // Support for immutable Collections (should improve recomposing performance) TODO: Does it?
            implementation(libs.kotlinx.immutableCollections)

            // Koin Dependency Injection
            api(libs.koin)
            implementation(libs.koin.compose)

            // Kotlinx Datetime -> Crossplatform DateTime implementation
            implementation(libs.kotlinx.datetime)

            // Ktor -> used to do web requests
            implementation(libs.ktor)

            // Multiplatform Settings
            implementation(libs.multiplatformSettings)

            // Multiplatform Resources (moko resources)
            /*api(libs.moko.resources)
            api(libs.moko.resources.compose)*/
            implementation(compose.components.resources)

            // Window size classes
            implementation(libs.windowSizeClass)

            // Insetsx -> Provides paddings respecting on-screen keyboards
            //TODO: Does stuff work without it?
            //implementation("com.moriatsushi.insetsx:insetsx:0.1.0-alpha10")

            // Precompose -> Multiplatform Navigation
            api(libs.precompose)
            api(libs.precompose.viewModel)
            api(libs.precompose.koin)
        }

        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            dependencies {

            }
        }

        val nonWebMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                // Sqldelight coroutines extension
                /*implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)*/

                // Multiplatform Settings Coroutines extension
                implementation(libs.multiplatformSettings.coroutines)

                // Ktor CIO engine
                implementation(libs.ktor.cio)
            }
        }

        val javaMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.skrapeit)
            }

        }

        val androidMain by getting {
            dependsOn(nonWebMain)
            dependsOn(javaMain)
            dependencies {
                // Compose UI
                implementation(libs.compose.ui)
                implementation(libs.compose.ui.tooling.preview)

                // Compose Multiplatform
                api(libs.androidx.activity.compose)
                api(libs.androidx.appcompat)
                api(libs.androidx.core.ktx)

                // Android Database Driver
                //implementation(libs.sqldelight.driver.android)

                // Firebase Messaging
                implementation(libs.firebase.messaging)

                // Android implementation of FlowSettings
                implementation(libs.multiplatformSettings.datastore)
                implementation(libs.androidx.datastore)

                // Preview Android Composables
                implementation(compose.preview)

                // Koin Android
                implementation(libs.koin.android)
            }
        }

        iosMain {
            dependsOn(nonWebMain)

            dependencies {
                //Sqldelight iOS database driver
                //implementation(libs.sqldelight.driver.native)

                //Ktor iOS client - needed for TLS sessions
                implementation(libs.ktor.darwin)
            }
        }

        val desktopMain by getting {
            dependsOn(nonWebMain)
            dependsOn(javaMain)
            dependencies {
                // Compose Multiplatform dependencies
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(compose.preview)

                // Sqldeight Desktop Driver
                //implementation(libs.sqldelight.driver.sqlite)

                // Kotlinx coroutines
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

android {
    namespace = "de.xorg.gsapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "de.xorg.gsapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.gsapp.versionCode.get().toInt()
        versionName = libs.versions.gsapp.versionName.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "mozilla/public-suffix-list.txt"
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }

        release {
            isMinifyEnabled = false
            /*isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")*/
        }
    }

    kotlin {
        jvmToolchain(11)
    }

    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GSApp"
            packageVersion = libs.versions.gsapp.versionName.get()
        }
    }
}


/*sqldelight {
    databases {
        create("GsAppDatabase") {
            packageName.set("de.xorg.gsapp.data.sql")
        }
    }
    linkSqlite.set(true)
}*/

afterEvaluate {
    tasks.withType<JavaExec> {
        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED")

        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }
    }
}

compose.experimental {
    web.application {}
}