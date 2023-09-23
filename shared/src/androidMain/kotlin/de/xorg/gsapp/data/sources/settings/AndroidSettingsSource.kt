package de.xorg.gsapp.data.sources.settings

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

actual typealias PlatformSettings = Application

private fun PlatformSettings.getPrefs(): SharedPreferences {
    return this.getSharedPreferences("", MODE_PRIVATE)
}

actual suspend fun PlatformSettings.getInt(key: String, default: Int): Int {
    return getPrefs().getInt(key, default)
}

actual suspend fun PlatformSettings.setInt(key: String, value: Int) {
    val editor = getPrefs().edit()
    editor.putInt(key, value)
    editor.apply()
}

actual suspend fun PlatformSettings.getString(key: String, default: String): String {
    return getPrefs().getString(key, default) ?: default
}

actual suspend fun PlatformSettings.setString(key: String, value: String) {
    val editor = getPrefs().edit()
    editor.putString(key, value)
    editor.apply()
}