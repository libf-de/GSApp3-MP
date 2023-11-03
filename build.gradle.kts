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
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id(Deps.GoogleServices._plugin).version(Versions.GoogleServices).apply(false)
    id(Deps.Sqldelight._plugin).version(Versions.Sqldelight).apply(false)
    id(Deps.SonarQube._plugin).version(Versions.SonarQube)
}

buildscript {
    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath(Deps.Moko.Resources.Generator)
        classpath(Deps.GoogleServices.Core)
    }
}