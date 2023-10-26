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

package de.xorg.gsapp.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.xorg.gsapp.MainActivity
import de.xorg.gsapp.R
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.state.FilterRole
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class GSAppFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {
    private val TAG = "GSAppFMS"
    private val CHANNEL_ID = "GsappSubstitutions"
    private val NTF_ID = 69420

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val prefRepo: PreferencesRepository by inject()

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Don't need to create the channel if it already exists
            if(notificationManager
                .notificationChannels
                .firstOrNull { it.id == CHANNEL_ID } != null) return


            val name = MR.strings.push_channel_name.desc().toString(this)
            val descriptionText = MR.strings.push_channel_desc.desc().toString(this)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     *
     * NOTE: The app does NOT use token-based messaging, it only uses topic-based messaging!
     * Thus new tokens aren't saved anywhere.
     */
    override fun onNewToken(token: String) {
        //Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //TODO: Improve notification!

        val serviceInstance = this

        scope.launch {
            val pushState = prefRepo.getPushFlow().lastOrNull() ?: PushState.default

            if(pushState == PushState.DISABLED) return@launch

            if(pushState == PushState.LIKE_FILTER && remoteMessage.data.isNotEmpty()) {
                val role = FilterRole.fromInt(
                    getSharedPreferences("GSApp", MODE_PRIVATE).getInt("role", FilterRole.default.value)
                )
                val filter = getSharedPreferences("GSApp", MODE_PRIVATE)
                    .getString("filter", "") ?: ""
                if(role == FilterRole.TEACHER && remoteMessage.data["teachers"]?.contains(filter) != true) return@launch
                if(role == FilterRole.STUDENT && remoteMessage.data["classes"]?.contains(filter) != true) return@launch
            }

            createNotificationChannel()

            val intent = Intent(serviceInstance, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                serviceInstance,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE)


            val builder = NotificationCompat.Builder(serviceInstance, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(MR.strings.push_notification_title.desc().toString(serviceInstance))
                .setContentText(MR.strings.push_notification_body.desc().toString(serviceInstance))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            if (ActivityCompat.checkSelfPermission(
                    serviceInstance,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Remind user to give notification permission
                prefRepo.setAskUserForNotificationPermission(true)
                Log.w(TAG, "WARN: No notification permission on receivedNotification!")
                return@launch
            }

            with(NotificationManagerCompat.from(serviceInstance)) {
                // notificationId is a unique int for each notification that you must define
                notify(NTF_ID, builder.build())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}