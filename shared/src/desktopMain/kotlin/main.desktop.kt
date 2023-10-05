import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.DbFood
import de.xorg.gsapp.data.DbSubject
import de.xorg.gsapp.data.di.mainModule
import de.xorg.gsapp.data.sql_adapters.DateAdapter
import de.xorg.gsapp.data.sql.GsAppDatabase
import de.xorg.gsapp.data.sql_adapters.ColorAdapter
import de.xorg.gsapp.data.sql_adapters.CommaSeparatedListAdapter
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.singleton
import java.io.File
import java.util.prefs.Preferences

actual fun getPlatformName(): String = "Desktop"

@Composable fun MainView() = withDI({
    bind<Settings>() with singleton { PreferencesSettings(
        Preferences.userRoot()
    ) }
    bind<SqlDriver>() with singleton {
        val dbPath = "gsapp.db"

        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbPath}")
        if(!File(dbPath).exists())
            GsAppDatabase.Schema.create(driver)
        driver
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