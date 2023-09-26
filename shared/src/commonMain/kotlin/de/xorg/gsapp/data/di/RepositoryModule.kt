package de.xorg.gsapp.data.di

import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.repositories.AppRepository
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.sources.local.JsonDataSource
import de.xorg.gsapp.data.sources.local.PathSource
import de.xorg.gsapp.data.sources.remote.GsWebsiteDataSource
import de.xorg.gsapp.ui.viewmodels.GSAppViewModel
import de.xorg.gsapp.ui.viewmodels.SettingsViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val repositoryModule = DI.Module("repositoryModule") {
    bind<GsWebsiteDataSource>() with singleton { GsWebsiteDataSource() }
    bind<PathSource>() with singleton { PathSource(di) }
    bind<JsonDataSource>() with singleton { JsonDataSource(instance()) }
    bind<GSAppRepository>() with singleton { AppRepository(di) }
}

val mainModule = DI.Module("mainModule") {
    import(repositoryModule)
    bind<PushNotificationUtil>() with singleton { PushNotificationUtil(di) }
    bind<GSAppViewModel>() with singleton { GSAppViewModel(di) }
    bind<SettingsViewModel>() with singleton { SettingsViewModel(di) }
}