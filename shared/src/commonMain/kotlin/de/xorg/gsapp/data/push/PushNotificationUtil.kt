package de.xorg.gsapp.data.push

import org.kodein.di.DI

expect class PushNotificationUtil(di: DI) {

    val isSupported: Boolean

    fun enablePushService(callback: (success: Boolean) -> Unit)

    fun disablePushService(callback: (success: Boolean) -> Unit)

    fun ensurePushPermissions(callback: (success: Boolean) -> Unit)
}