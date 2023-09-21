package de.xorg.gsapp.data.push

import org.kodein.di.DI

actual class PushNotificationUtil actual constructor(di: DI) {
    actual fun enablePushService(callback: (success: Boolean) -> Unit) {
        callback(false)
    }

    actual fun disablePushService(callback: (success: Boolean) -> Unit) {
        callback(false)
    }

    actual fun ensurePushPermissions(callback: (success: Boolean) -> Unit) {
        callback(false)
    }

    actual val isSupported: Boolean = false
}