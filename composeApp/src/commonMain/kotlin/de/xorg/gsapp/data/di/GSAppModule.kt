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

package de.xorg.gsapp.data.di

import de.xorg.gsapp.data.repositories.AppRepository
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.repositories.MockSettingsRepository
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.data.sources.defaults.DefaultsDataSource
import de.xorg.gsapp.data.sources.defaults.GsDefaultsSource
import de.xorg.gsapp.data.sources.local.LocalDataSource
import de.xorg.gsapp.data.sources.local.MockDataSource
import de.xorg.gsapp.data.sources.remote.DebugDataSource
import de.xorg.gsapp.data.sources.remote.RemoteDataSource
import de.xorg.gsapp.ui.tools.JobTool
import de.xorg.gsapp.ui.viewmodels.ExamPlanViewModel
import de.xorg.gsapp.ui.viewmodels.FoodplanViewModel
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import de.xorg.gsapp.ui.viewmodels.SubstitutionPlanViewModel
import kotlinx.coroutines.Job
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val JOB_MAP = "jobMap"

fun appModule() = listOf(dataRepositoryModule, preferencesRepositoryModule, commonModule)

val dataRepositoryModule = module {
    single<MutableMap<String, Job>>(named(JOB_MAP)) {
        mutableMapOf()
    }

    single { JobTool() }

    single<LocalDataSource> { MockDataSource() }
    //single<RemoteDataSource> { WebsiteDataSource() }
    single<RemoteDataSource> { DebugDataSource() }
    single<DefaultsDataSource> { GsDefaultsSource() }
    single<GSAppRepository> { AppRepository() }
}

val preferencesRepositoryModule = module {
    single<PreferencesRepository> { MockSettingsRepository() }
}

val commonModule = module {
    factory { SubstitutionPlanViewModel() }
    factory { FoodplanViewModel() }
    factory { ExamPlanViewModel() }

    singleOf(::SettingsViewModel)
}