import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.cache.AndroidCacheManager
import de.xorg.gsapp.data.cache.CacheManager
import de.xorg.gsapp.data.di.mainModule
import de.xorg.gsapp.data.sql.GsAppDatabase
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.provider
import org.kodein.di.singleton

actual fun getPlatformName(): String = "Android"

@Composable fun MainView(ctx: Activity) = withDI({
    bind<SqlDriver>() with singleton {
        AndroidSqliteDriver(GsAppDatabase.Schema, ctx, "gsapp.db")
    }
    bind<Activity>() with provider { ctx }
    bind<CacheManager>() with singleton { AndroidCacheManager(ctx) }
    bind<Settings>() with singleton { SharedPreferencesSettings(
        ctx.getSharedPreferences("GSApp", MODE_PRIVATE)
    )}
    import(mainModule)
}) {

    GSApp()
}
