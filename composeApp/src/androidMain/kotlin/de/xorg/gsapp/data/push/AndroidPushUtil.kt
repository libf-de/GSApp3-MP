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
import com.google.firebase.messaging.ktx.messaging
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.ui.tools.MakeToast
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.push_disabled_failure
import gsapp.composeapp.generated.resources.push_disabled_success
import gsapp.composeapp.generated.resources.push_enabled_failure
import gsapp.composeapp.generated.resources.push_enabled_failure_timeout
import gsapp.composeapp.generated.resources.push_enabled_success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
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

                    Log.d(TAG, "Push enabled done - success=${task.isSuccessful}")

                    MakeToast(
                        context = context,
                        message = if(task.isSuccessful)
                            Res.string.push_enabled_success
                        else
                            Res.string.push_enabled_failure
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        val msg = getString(
                            if(task.isSuccessful)
                                Res.string.push_enabled_success
                            else
                                Res.string.push_enabled_failure
                        )

                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }


                }
                .addOnFailureListener {
                    callback(false)
                }
                .addOnCanceledListener {
                    callback(false)
                }
        } catch(ex: IllegalStateException) {
            MakeToast(
                context = context,
                message = Res.string.push_enabled_failure_timeout
            )

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

                    Log.d(TAG, "Push disabled done - success=${task.isSuccessful}")

                    MakeToast(
                        context = context,
                        message = if(task.isSuccessful)
                            Res.string.push_disabled_success
                        else
                            Res.string.push_disabled_failure
                    )
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