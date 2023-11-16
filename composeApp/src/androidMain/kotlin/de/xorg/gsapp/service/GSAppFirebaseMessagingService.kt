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
import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.repositories.GSAppRepository
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.state.PushState
import de.xorg.gsapp.ui.tools.DateUtil
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class GSAppFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {
    private val TAG = "GSAppFMS"
    private val CHANNEL_ID = "GSAppSubstitutions"
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
            if (notificationManager
                    .notificationChannels
                    .firstOrNull { it.id == CHANNEL_ID } != null
            ) return


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
     * Thus, new tokens aren't saved anywhere.
     */
    override fun onNewToken(token: String) {
        // Ignore new tokens
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        scope.launch {
            handleMessage(remoteMessage)
        }

    }

    private suspend fun handleMessage(remoteMessage: RemoteMessage) {
        println("in handleMessage")
        val pushState = prefRepo.getPush()
        val filter = prefRepo.getFilter()
        //val pushState = prefRepo.getPushFlow().lastOrNull() ?: PushState.default
        //val filter = prefRepo.getFilterFlow().lastOrNull() ?: Filter.NONE
        println("in after load settings")

        if (pushState == PushState.DISABLED) {
            println("push is disabled :/")
            return
        }

        if (pushState == PushState.LIKE_FILTER &&
            remoteMessage.data.isNotEmpty()
        ) {
            println("in like filter")
            // Don't notify (=return) if FilterRole matches, but remote data doesn't contain the filter value
            if (filter.role == Filter.Role.TEACHER &&
                remoteMessage.data["teachers"]?.contains(filter.value) != true
            ) return

            if (filter.role == Filter.Role.STUDENT &&
                remoteMessage.data["classes"]?.contains(filter.value) != true
            ) return
            println("out like filter")
        }

        println("before repository")
        val appRepository: GSAppRepository by inject()
        appRepository.updateSubstitutions {
            if (it.isSuccess) {
                // Update was successful -> show preview in notification
                scope.launch {
                    appRepository.getSubstitutions()
                        .catch { e ->
                            Napier.w("Failed to get substitutions", e)
                        }
                        .collectLatest { subSet ->
                            postSubstitutionNotification(
                                context = this@GSAppFirebaseMessagingService,
                                substitutionSet = subSet,
                                filter = filter
                            )
                        }
                }
            } else {
                postSubstitutionNotification(context = this@GSAppFirebaseMessagingService)
            }


            Log.d(
                TAG, "Updated substitution plan " + (if (it.isSuccess) "successfully" else "")
            )
        }

    }

    private fun postSubstitutionNotification(
        context: Context,
        substitutionSet: SubstitutionSet? = null,
        filter: Filter? = null
    ) {
        createNotificationChannel()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Remind user to give notification permission
            scope.launch { prefRepo.setAskUserForNotificationPermission(true) }
            Log.w(TAG, "WARN: No notification permission on receivedNotification!")

            // We can't post a notification :/
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )


        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(MR.strings.push_notification_title.desc().toString(context))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (substitutionSet != null && filter != null && filter.role != Filter.Role.ALL) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val dayDelta = today.daysUntil(substitutionSet.date)

            val relLabel: String = when (dayDelta) {
                0 -> MR.strings.rel_today.getString(context)
                1 -> MR.strings.rel_tomorrow.getString(context)
                2 -> MR.strings.rel_after_tomorrow.getString(context)
                3, 4, 5, 6, 7 -> {
                    val localizedDay = DateUtil
                        .getWeekdayLongRes(substitutionSet.date)
                        .desc()
                        .toString(context)
                    MR.strings.rel_next_weekday.format(localizedDay).toString(context)
                }

                else -> MR.strings.rel_absolute.format(
                    DateUtil.getDateAsString(substitutionSet.date) { it.desc().toString(context) }
                ).toString(context)
            }

            val subList = if (filter.role == Filter.Role.STUDENT) {
                substitutionSet.substitutions[filter.value] ?: emptyList()
            } else {
                substitutionSet.substitutions.flatMap {
                    it.value.filter { sub ->
                        sub.substTeacher.shortName.lowercase() == filter.value.lowercase()
                    }
                }
            }

            MR.strings.push_notification_detail_amount.format(
                subList.size,
                relLabel
            ).toString(context).also {
                builder = builder.setContentText(it)
            }
        } else {
            builder = builder.setContentText(MR.strings.push_notification_body.desc().toString(context))
        }

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(NTF_ID, builder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}