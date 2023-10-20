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

package de.xorg.gsapp.data.di

import app.cash.sqldelight.EnumColumnAdapter
import de.xorg.gsapp.data.DbExam
import de.xorg.gsapp.data.DbFood
import de.xorg.gsapp.data.DbSubject
import de.xorg.gsapp.data.DbSubstitutionSet
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.repositories.AppRepository
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.sources.defaults.DefaultsDataSource
import de.xorg.gsapp.data.sources.defaults.GsDefaultsSource
import de.xorg.gsapp.data.sources.local.LocalDataSource
import de.xorg.gsapp.data.sources.local.SqldelightDataSource
import de.xorg.gsapp.data.sources.remote.RemoteDataSource
import de.xorg.gsapp.data.sources.remote.WebsiteDataSource
import de.xorg.gsapp.data.sql.GsAppDatabase
import de.xorg.gsapp.data.sql_adapters.ColorAdapter
import de.xorg.gsapp.data.sql_adapters.CommaSeparatedListAdapter
import de.xorg.gsapp.data.sql_adapters.DateAdapter
import de.xorg.gsapp.ui.viewmodels.GSAppViewModel
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val mainModule = DI.Module("mainModule") {
    bind<GsAppDatabase>() with singleton {
        GsAppDatabase(
            driver = instance(),
            DbFoodAdapter = DbFood.Adapter(
                dateAdapter = DateAdapter,
                additivesAdapter = CommaSeparatedListAdapter
            ),
            DbSubjectAdapter = DbSubject.Adapter(
                colorAdapter = ColorAdapter
            ),
            DbExamAdapter = DbExam.Adapter(
                courseAdapter = EnumColumnAdapter(),
                dateAdapter = DateAdapter
            ),
            DbSubstitutionSetAdapter = DbSubstitutionSet.Adapter(
                dateAdapter = DateAdapter
            )
        )
    }
    //bind<RemoteDataSource>() with singleton { DebugWebDataSource() }
    bind<RemoteDataSource>() with singleton { WebsiteDataSource(di) }
    bind<LocalDataSource>() with singleton { SqldelightDataSource(di) }
    bind<DefaultsDataSource>() with singleton { GsDefaultsSource(di) }
    bind<GSAppRepository>() with singleton { AppRepository(di) }
    bind<PushNotificationUtil>() with singleton { PushNotificationUtil(di) }
    bind<GSAppViewModel>() with singleton { GSAppViewModel(di) }
    bind<SettingsViewModel>() with singleton { SettingsViewModel(di) }
}