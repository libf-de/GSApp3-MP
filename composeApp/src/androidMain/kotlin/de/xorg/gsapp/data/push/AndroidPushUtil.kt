/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023. Fabian Schillig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.xorg.gsapp.data.push

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.res.MR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidPushUtil : PushNotificationUtil, KoinComponent {

    private val context: Context by inject()
    private val prefRepo: PreferencesRepository by inject()
    private val TAG = "AndroidPushUtil"

    override val isSupported: Boolean = true

    /*
     * This is a workaround to check if FirebaseApp was deleted.
     * See {@link disablePushService} for more information.
     */
    private fun isFirebaseDeleted(): Boolean {
        return try {
            FirebaseApp.getInstance().applicationContext
            false
        } catch (ex: IllegalStateException) {
            true
        }
    }

    override fun enablePushService(callback: (success: Boolean) -> Unit) {
        if(isFirebaseDeleted() || FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        try {
            // This is used to ensure FirebaseApp is not deleted, see {@link disablePushService}
            FirebaseApp.getInstance().applicationContext

            Firebase.messaging.isAutoInitEnabled = true
            Firebase.messaging.subscribeToTopic("substitutions")
                .addOnCompleteListener { task ->
                    callback(task.isSuccessful)
                    var msg = MR.strings.push_enabled_success.getString(context)
                    if (!task.isSuccessful) {
                        msg = MR.strings.push_enabled_failure.getString(context)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    callback(false)
                }
                .addOnCanceledListener {
                    callback(false)
                }
        } catch(ex: IllegalStateException) {
            Toast.makeText(
                context,
                MR.strings.push_enabled_failure_timeout.getString(context),
                Toast.LENGTH_SHORT
            ).show()
            Log.e(TAG, "FirebaseApp did not initialize in time :/")
            ex.printStackTrace()
            callback(false)
            return
        }
    }

    override fun disablePushService(callback: (success: Boolean) -> Unit) {
        try {
            // This is used to check if FirebaseApp is not deleted
            // I don't think there is a public method to check this, and the
            // app crashes in Firebase code if the instance was deleted, without
            // any way to catch the exception. So just try to access the context,
            // which fails (HERE) with a IllegalStateException if the instance was
            // deleted.
            FirebaseApp.getInstance().applicationContext

            // Delete Firebase Instance ID
            Firebase.messaging.deleteToken()

            // Disable FCM auto-init
            Firebase.messaging.isAutoInitEnabled = false

            Firebase.messaging.unsubscribeFromTopic("substitutions")
                .addOnCompleteListener { task ->
                    // Uninitialize FirebaseApp
                    FirebaseApp.getInstance().delete()

                    callback(task.isSuccessful)
                    var msg = MR.strings.push_disabled_success.getString(context)
                    if (!task.isSuccessful) {
                        msg = MR.strings.push_disabled_failure.getString(context)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    callback(false)
                }
                .addOnCanceledListener {
                    callback(false)
                }
        } catch(ex: IllegalStateException) {
            Log.e(TAG, "FirebaseApp did not initialize in time :/")
            ex.printStackTrace()
            callback(false)
            return
        }
    }

    override fun ensurePushPermissions(callback: (success: Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            CoroutineScope(Dispatchers.IO).launch {
                prefRepo.setAskUserForNotificationPermission(
                    ContextCompat
                        .checkSelfPermission(context, POST_NOTIFICATIONS) != PERMISSION_GRANTED
                )
            }
        }

        callback(true)
    }
}