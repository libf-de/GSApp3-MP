import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.moriatsushi.insetsx.WindowInsetsUIViewController
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.mainModule
import de.xorg.gsapp.data.sql.GsAppDatabase
import moe.tlaster.precompose.PreComposeApplication
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.singleton
import platform.Foundation.NSUserDefaults.Companion.standardUserDefaults

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = WindowInsetsUIViewController { PreComposeApplication {
    withDI({
        bind<Settings>() with singleton { NSUserDefaultsSettings(standardUserDefaults()) }
        bind<SqlDriver>() with singleton {
            NativeSqliteDriver(GsAppDatabase.Schema, "gsapp.db")
        }

        //bind<SettingsSource>() with singleton { SettingsSource(NSObject()) }
        import(mainModule)
    }) { GSApp() }
} }
