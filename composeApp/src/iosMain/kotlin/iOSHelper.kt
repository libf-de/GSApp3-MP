import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import de.xorg.gsapp.data.di.appModule
import de.xorg.gsapp.data.push.PushNotificationUtil
import de.xorg.gsapp.data.push.iosPushUtilStub
import de.xorg.gsapp.data.sources.remote.GsWebsiteParser
import de.xorg.gsapp.data.sources.remote.IosWebsiteParser
import de.xorg.gsapp.data.sql.GsAppDatabase
import de.xorg.gsapp.ui.tools.PlatformInterface
import de.xorg.gsapp.ui.tools.IOSPlatformImpl
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import de.xorg.gsapp.data.di.iosModule


fun initKoin() {
    startKoin {
        modules(appModule() + iosModule)
    }
}