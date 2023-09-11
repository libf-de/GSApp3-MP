import android.content.Context
import androidx.compose.runtime.Composable
import de.xorg.gsapp.GSApp
import de.xorg.gsapp.data.cache.AndroidCacheManager
import de.xorg.gsapp.data.cache.CacheManager
import de.xorg.gsapp.data.di.mainModule
import org.kodein.di.bind
import org.kodein.di.compose.withDI
import org.kodein.di.singleton

actual fun getPlatformName(): String = "Android"

@Composable fun MainView(ctx: Context) = withDI({
    bind<CacheManager>() with singleton { AndroidCacheManager(ctx) }
    import(mainModule)
}) {

    GSApp()
}
