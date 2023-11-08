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

object Deps {
    object AndroidX {
        const val ActivityCompose = "androidx.activity:activity-compose:${Versions.AndroidX.ActivityCompose}"
        const val AppCompat = "androidx.appcompat:appcompat:${Versions.AndroidX.AppCompat}"
        const val CoreKtx = "androidx.core:core-ktx:${Versions.AndroidX.CoreKtx}"
        const val DatastorePreferences = "androidx.datastore:datastore-preferences:${Versions.AndroidX.DatastorePreferences}"
    }

    const val FirebaseMessaging = "com.google.firebase:firebase-messaging-ktx:${Versions.FirebaseMessaging}"
    object GoogleServices {
        const val Core = "com.google.gms:google-services:${Versions.GoogleServices}"
        const val _plugin = "com.google.gms.google-services"
    }

    object KotlinX {
        const val Coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KotlinX.coroutines}"
        const val CoroutinesSwing = "org.jetbrains.kotlinx:kotlinx-coroutines-swing:${Versions.KotlinX.coroutines}"
        const val ImmutableCollections = "org.jetbrains.kotlinx:kotlinx-collections-immutable:${Versions.KotlinX.immutableCollections}"
        const val Datetime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.KotlinX.datetime}"
    }

    object Koin {
        const val Core = "io.insert-koin:koin-core:${Versions.Koin.core}"
        const val Compose = "io.insert-koin:koin-compose:${Versions.Koin.compose}"
        const val Android = "io.insert-koin:koin-android:${Versions.Koin.core}"
    }

    object Ktor {
        const val Core = "io.ktor:ktor-client-core:${Versions.Ktor}"
        const val Cio = "io.ktor:ktor-client-cio:${Versions.Ktor}"
        const val Darwin = "io.ktor:ktor-client-darwin:${Versions.Ktor}"
    }

    object Moko {
        object Resources {
            const val Core = "dev.icerock.moko:resources:${Versions.Moko.Resources}"
            const val Compose = "dev.icerock.moko:resources-compose:${Versions.Moko.Resources}"
            const val Generator = "dev.icerock.moko:resources-generator:${Versions.Moko.Resources}"
            const val Test = "dev.icerock.moko:resources-test:${Versions.Moko.Resources}"
            const val _plugin = "dev.icerock.mobile.multiplatform-resources"
        }


        const val Graphics = "dev.icerock.moko:graphics:${Versions.Moko.Graphics}"
    }

    object MultiplatformSettings {
        const val Core = "com.russhwolf:multiplatform-settings:${Versions.MultiplatformSettings}"
        const val Coroutines = "com.russhwolf:multiplatform-settings-coroutines:${Versions.MultiplatformSettings}"
        const val Datastore = "com.russhwolf:multiplatform-settings-datastore:${Versions.MultiplatformSettings}"
    }

    const val SkrapeIt = "it.skrape:skrapeit:${Versions.SkrapeIt}"

    object SonarQube {
        const val _plugin = "org.sonarqube"
    }

    object Sqldelight {
        const val Coroutines = "app.cash.sqldelight:coroutines-extensions:${Versions.Sqldelight}"
        const val AndroidDriver = "app.cash.sqldelight:android-driver:${Versions.Sqldelight}"
        const val NativeDriver = "app.cash.sqldelight:native-driver:${Versions.Sqldelight}"
        const val SqliteDriver = "app.cash.sqldelight:sqlite-driver:${Versions.Sqldelight}"
        const val _plugin = "app.cash.sqldelight"
    }

    object Precompose {
        const val Core = "moe.tlaster:precompose:${Versions.Precompose}"
        const val ViewModel = "moe.tlaster:precompose-viewmodel:${Versions.Precompose}"
        const val Koin = "moe.tlaster:precompose-koin:${Versions.Precompose}"
    }

    const val Logging = "org.lighthousegames:logging:${Versions.Logging}"
    const val WindowSizeClass = "dev.chrisbanes.material3:material3-window-size-class-multiplatform:${Versions.WindowSizeClass}"

}

object Versions {
    object AndroidX {
        const val ActivityCompose = "1.7.2"
        const val AppCompat = "1.6.1"
        const val CoreKtx = "1.10.1"
        const val DatastorePreferences = "1.0.0"
    }

    const val FirebaseMessaging = "23.2.1"
    const val GoogleServices = "4.3.5"

    object Kotlin {
        const val CompilerExtension = "1.5.3"
    }

    object KotlinX {
        const val coroutines = "1.7.2"
        const val immutableCollections = "0.3.5"
        const val datetime = "0.4.1"
    }

    object Koin {
        const val core = "3.5.0"
        const val compose = "1.1.0"
    }

    const val Ktor = "2.3.4"
    object Moko {
        const val Resources = "0.23.0"
        const val Graphics = "0.9.0"
    }

    const val MultiplatformSettings = "1.1.0"
    const val SkrapeIt = "1.2.2"
    const val SonarQube = "4.4.1.3373"
    const val Sqldelight = "2.0.0"
    const val WindowSizeClass = "0.3.1"
    const val Precompose = "1.5.1"
    const val Logging = "1.3.0"
}