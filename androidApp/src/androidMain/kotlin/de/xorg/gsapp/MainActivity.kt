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

package de.xorg.gsapp

import MainView
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.state.PushState
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module


class MainActivity : PreComposeActivity() {

    private val activityModule = module {
        single<Activity> { this@MainActivity }
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    private suspend fun askNotificationPermission(prefRepo: PreferencesRepository) {
        // This is only necessary for API level >= 33 (TIRAMISU)

        //val pushState = prefRepo.getPushFlow().lastOrNull() ?: PushState.default
        val pushState = prefRepo.getPush()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                println("permission granted")
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
                println("show dialog")

                prefRepo.setAskUserForNotificationPermission(true)

                /*AlertDialog.Builder(this)
                    .setTitle(MR.strings.push_no_permission_title.desc().toString(this))
                    .setMessage(MR.strings.push_no_permission_body.desc().toString(this))
                    .setPositiveButton(MR.strings.push_no_permission_fix.desc().toString(this)
                    ) { di, _ ->
                        val intent = Intent()
                        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                        intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
                        intent.putExtra("app_package", packageName)
                        intent.putExtra("app_uid", applicationInfo.uid)
                        startActivity(intent)
                        di.dismiss()
                    }
                    .setNegativeButton(MR.strings.push_no_permission_later.desc().toString()) {
                        di, _ -> di.dismiss()
                    }.create().show()*/
            } else {
                println("askForPermission")

                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        loadKoinModules(activityModule)

        setContent {
            MainView()
        }
    }

    override fun onResume() {
        super.onResume()
        val prefRepo: PreferencesRepository by inject()

        scope.launch {
            prefRepo.getPushFlow().collect {
                if(it == PushState.DISABLED) return@collect
                askNotificationPermission(prefRepo)
            }
            /*prefRepo.getAskUserForNotificationPermissionFlow().collect {
                if(it) {
                    askNotificationPermission(prefRepo)
                }
            }*/
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        unloadKoinModules(activityModule)
    }
}