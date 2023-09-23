package de.xorg.gsapp.data.sources.settings

import platform.Foundation.NSUserDefaults
import platform.darwin.NSObject

actual typealias PlatformSettings = NSObject

actual suspend fun PlatformSettings.getInt(key: String, default: Int): Int {
    return try {
        NSUserDefaults.standardUserDefaults.getInt(key)
    } catch(ex: Exception) {
        ex.printStackTrace()
        default
    }

}

actual suspend fun PlatformSettings.setInt(key: String, value: Int) {
    NSUserDefaults.standardUserDefaults.setInt(key, value)
}

actual suspend fun PlatformSettings.getString(key: String, default: String): String {
    return try {
        NSUserDefaults.standardUserDefaults.getString(key)
    } catch(ex: Exception) {
        ex.printStackTrace()
        default
    }
}

actual suspend fun PlatformSettings.setString(key: String, value: String) {
    NSUserDefaults.standardUserDefaults.setString(key, value)
}