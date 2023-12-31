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
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import de.xorg.gsapp.data.repositories.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidPushUtil : PushNotificationUtil, KoinComponent {

    //private val activity: Activity by inject()
    private val context: Context by inject()
    private val prefRepo: PreferencesRepository by inject()

    override val isSupported: Boolean = true

    override fun enablePushService(callback: (success: Boolean) -> Unit) {
        // Enable FCM auto-init
        Firebase.messaging.isAutoInitEnabled = true

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

    override fun disablePushService(callback: (success: Boolean) -> Unit) {
        // Disable FCM auto-init
        Firebase.messaging.isAutoInitEnabled = false

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

    override fun ensurePushPermissions(callback: (success: Boolean) -> Unit) {
        var granted = false;

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