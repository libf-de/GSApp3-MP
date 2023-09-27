import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.di.mainModule
import de.xorg.gsapp.data.sql.GsAppDatabase
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.singleton
import java.util.prefs.Preferences

actual fun getPlatformName(): String = "Desktop"

@Composable fun MainView() = withDI({
    bind<Settings>() with singleton { PreferencesSettings(
        Preferences.userRoot()
    ) }
    bind<GsAppDatabase>() with singleton {
        GsAppDatabase(
            JdbcSqliteDriver("jdbc:sqlite:gsapp.db")
        )
    }
    import(mainModule)
}) {
    GSApp()
}

@Preview
@Composable
fun AppPreview() {
    App()
}