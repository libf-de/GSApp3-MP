package de.xorg.gsapp.data.sources.settings

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import net.harawata.appdirs.AppDirsFactory

data class DesktopPrefs(
    val intPrefs: Map<String, Int>,
    val stringPrefs: Map<String, String>
)

class DesktopSettings {
    private val appDir: String = AppDirsFactory
        .getInstance()
        .getUserCacheDir("gsapp", "3", "de.xorg")
    var desktopStore: KStore<DesktopPrefs> = storeOf(
        filePath = "${appDir}/preferences.json"
    )
}


actual typealias PlatformSettings = DesktopSettings

actual suspend fun PlatformSettings.getInt(key: String, default: Int): Int {
    val intVals = desktopStore.get()?.intPrefs ?: emptyMap()
    return intVals[key] ?: default
}

actual suspend fun PlatformSettings.setInt(key: String, value: Int) {
    val intVals: Map<String, Int> = (desktopStore.get()?.intPrefs ?: emptyMap())
        .toMutableMap().apply {
        this[key] = value
    }
    desktopStore.update {
        DesktopPrefs(intVals, it?.stringPrefs ?: emptyMap())
    }
}

actual suspend fun PlatformSettings.getString(key: String, default: String): String {
    val stringVals = desktopStore.get()?.stringPrefs ?: emptyMap()
    return stringVals[key] ?: default
}

actual suspend fun PlatformSettings.setString(key: String, value: String) {
    val stringVals: Map<String, String> = (desktopStore.get()?.stringPrefs ?: emptyMap())
        .toMutableMap().apply {
            this[key] = value
        }
    desktopStore.update {
        DesktopPrefs(it?.intPrefs ?: emptyMap(), stringVals)
    }
}