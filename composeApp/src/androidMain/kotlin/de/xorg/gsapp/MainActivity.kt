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

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.ui.state.PushState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    /**
     * Verify that the user has granted the notification permission, and if not, ask for it.
     * Should only be called if push notifications are enabled.
     * @param prefRepo the preferences repository, to set flag to ask user for notification permission
     */
    private suspend fun askNotificationPermission(prefRepo: PreferencesRepository) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // Set the flag-setting to true, so that we can show the user an educational UI
                // later explaining that push notifications require the notification permission
                prefRepo.setAskUserForNotificationPermission(true)
            } else {
                // Directly ask for notification permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        loadKoinModules(activityModule)

        setContent {
            GSApp()
        }
    }

    override fun onResume() {
        super.onResume()
        val prefRepo: PreferencesRepository by inject()

        // If push notifications are enabled, verify that we have notification permission
        scope.launch {
            prefRepo.getPushFlow().collect {
                if(it == PushState.DISABLED) return@collect
                askNotificationPermission(prefRepo)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        unloadKoinModules(activityModule)
    }
}