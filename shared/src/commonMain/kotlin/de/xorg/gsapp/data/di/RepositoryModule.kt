package de.xorg.gsapp.data.di

import app.cash.sqldelight.EnumColumnAdapter
import de.xorg.gsapp.data.DbExam
import de.xorg.gsapp.data.DbFood
import de.xorg.gsapp.data.DbSubject
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.repositories.AppRepository
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.sources.local.JsonDataSource
import de.xorg.gsapp.data.sources.local.LocalDataSource
import de.xorg.gsapp.data.sources.local.PathSource
import de.xorg.gsapp.data.sources.local.SqldelightDataSource
import de.xorg.gsapp.data.sources.remote.DebugWebDataSource
import de.xorg.gsapp.data.sources.remote.GsWebsiteDataSource
import de.xorg.gsapp.data.sources.remote.RemoteDataSource
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

val repositoryModule = DI.Module("repositoryModule") {
    /*bind<RemoteDataSource>() with singleton { GsWebsiteDataSource() } TODO: Revert to production! */
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
            )
        )
    }
    bind<RemoteDataSource>() with singleton { DebugWebDataSource() }
    bind<PathSource>() with singleton { PathSource(di) }
    //bind<LocalDataSource>() with singleton { JsonDataSource(instance()) }
    bind<JsonDataSource>() with singleton { JsonDataSource(instance()) }
    bind<LocalDataSource>() with singleton { SqldelightDataSource(di) }
    bind<GSAppRepository>() with singleton { AppRepository(di) }
}

val mainModule = DI.Module("mainModule") {
    import(repositoryModule)
    bind<PushNotificationUtil>() with singleton { PushNotificationUtil(di) }
    bind<GSAppViewModel>() with singleton { GSAppViewModel(di) }
    bind<SettingsViewModel>() with singleton { SettingsViewModel(di) }
}