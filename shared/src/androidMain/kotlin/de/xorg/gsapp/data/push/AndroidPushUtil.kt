package de.xorg.gsapp.data.push

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import org.kodein.di.DI
import org.kodein.di.instance

actual class PushNotificationUtil actual constructor(di: DI) {

    private val context: Context by di.instance()

    actual val isSupported: Boolean = true

    actual fun enablePushService(callback: (success: Boolean) -> Unit) {
        Firebase.messaging.subscribeToTopic("substitutions")
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
                var msg = "Push enabled!"
                if (!task.isSuccessful) {
                    msg = "Failed to enable push :("
                }
                Log.d("AndroidPushUtil", msg)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
    }

    actual fun disablePushService(callback: (success: Boolean) -> Unit) {
        Firebase.messaging.unsubscribeFromTopic("substitutions")
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
                var msg = "Push disabled!"
                if (!task.isSuccessful) {
                    msg = "Failed to disable push :("
                }
                Log.d("AndroidPushUtil", msg)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
    }

    actual fun ensurePushPermissions(callback: (success: Boolean) -> Unit) {
    }
}