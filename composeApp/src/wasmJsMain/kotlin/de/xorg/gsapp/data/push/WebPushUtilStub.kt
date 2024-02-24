package de.xorg.gsapp.data.push

import org.koin.core.component.KoinComponent

class WebPushUtilStub : PushNotificationUtil, KoinComponent {
    override fun enablePushService(callback: (success: Boolean) -> Unit) {
        callback(false)
    }

    override fun disablePushService(callback: (success: Boolean) -> Unit) {
        callback(false)
    }

    override fun ensurePushPermissions(callback: (success: Boolean) -> Unit) {
        callback(false)
    }

    override val isSupported: Boolean = false
}