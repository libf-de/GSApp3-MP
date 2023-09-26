package de.xorg.gsapp

import MainView
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import moe.tlaster.precompose.lifecycle.setContent
import androidx.core.view.WindowCompat
import de.xorg.gsapp.ui.state.PushState
import moe.tlaster.precompose.lifecycle.PreComposeActivity

class MainActivity : PreComposeActivity() {
    private fun askNotificationPermission(sharedPrefs: SharedPreferences) {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {

                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
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
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val prefs: SharedPreferences = getSharedPreferences("GSApp", MODE_PRIVATE)

        askNotificationPermission(prefs)

        setContent {
            MainView(this)
        }
    }
}