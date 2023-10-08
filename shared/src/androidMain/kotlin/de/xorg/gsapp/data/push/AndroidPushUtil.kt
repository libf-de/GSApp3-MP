/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023 Fabian Schillig
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

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import de.xorg.gsapp.ui.state.PushState
import org.kodein.di.DI
import org.kodein.di.instance

actual class PushNotificationUtil actual constructor(di: DI) {

    private val activity: Activity by di.instance()

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
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
            }
    }

    actual fun ensurePushPermissions(callback: (success: Boolean) -> Unit) {
        // This is only necessary for API level >= 33 (TIRAMISU)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {

                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(activity, android.Manifest.permission.POST_NOTIFICATIONS)) {
                // display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the
                rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    if(!isGranted) callback(true)
                }
                registerForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { isGranted: Boolean ->
                    if(!isGranted && PushState.fromInt(
                            sharedPrefs.getInt("push", PushState.default.value)
                        ) != PushState.DISABLED) {
                        val editor = sharedPrefs.edit()
                        editor.putInt("push", PushState.DISABLED.value)
                        editor.apply()
                        Toast.makeText(this, "No notification permission -> Push disabled!", Toast.LENGTH_SHORT).show()
                    }
                }.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }*/
        //TODO: Implement!
        callback(true)
    }
}