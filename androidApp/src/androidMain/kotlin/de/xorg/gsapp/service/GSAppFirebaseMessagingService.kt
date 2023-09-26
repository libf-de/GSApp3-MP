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
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.state.FilterRole
import dev.icerock.moko.resources.desc.desc

class GSAppFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "GSAppFMS"
    private val CHANNEL_ID = "GsappSubstitutions"
    private val NTF_ID = 69420

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = MR.strings.push_channel_name.desc().toString(this)
            val descriptionText = MR.strings.push_channel_desc.desc().toString(this)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val pushState = PushState.fromInt(
            getSharedPreferences("GSApp", MODE_PRIVATE).getInt("push", PushState.default.value)
        )

        if(pushState == PushState.DISABLED) return

        if(pushState == PushState.LIKE_FILTER && remoteMessage.data.isNotEmpty()) {
            val role = FilterRole.fromInt(
                getSharedPreferences("GSApp", MODE_PRIVATE).getInt("role", FilterRole.default.value)
            )
            val filter = getSharedPreferences("GSApp", MODE_PRIVATE)
                                .getString("filter", "") ?: ""
            if(role == FilterRole.TEACHER && remoteMessage.data["teachers"]?.contains(filter) != true) return
            if(role == FilterRole.STUDENT && remoteMessage.data["classes"]?.contains(filter) != true) return
        }

        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(MR.strings.push_notification_title.desc().toString(this))
            .setContentText(MR.strings.push_notification_body.desc().toString(this))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "WARN: No notification permission on receivedNotification!")
            return
        }

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(NTF_ID, builder.build())
        }
    }
}