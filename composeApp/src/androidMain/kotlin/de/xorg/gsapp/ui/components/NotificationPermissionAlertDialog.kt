package de.xorg.gsapp.ui.components

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import de.xorg.gsapp.data.repositories.PreferencesRepository
import de.xorg.gsapp.ui.theme.GSAppTheme
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.push_no_permission_body
import gsapp.composeapp.generated.resources.push_no_permission_fix
import gsapp.composeapp.generated.resources.push_no_permission_later
import gsapp.composeapp.generated.resources.push_no_permission_title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun NotificationPermissionAlertDialog() {
    val context: Context = koinInject()
    val preferencesRepository: PreferencesRepository = koinInject()

    // Launcher to open notification settings
    val openPermissionSettings = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Don't show the dialog again if permission was granted
        if(ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) == PERMISSION_GRANTED)
            CoroutineScope(Dispatchers.IO).launch {
                preferencesRepository.setAskUserForNotificationPermission(false)
            }
    }

    // Whether to show the dialog
    val permissionAlertDialogState by koinInject<PreferencesRepository>()
        .getAskUserForNotificationPermissionFlow().collectAsStateWithLifecycle(initial = false)

    var permissionAlertDialogShow by remember { mutableStateOf(false) }



    LaunchedEffect(permissionAlertDialogState) {
        permissionAlertDialogShow = permissionAlertDialogState
    }

    if(permissionAlertDialogShow) {
        GSAppTheme {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(stringResource(Res.string.push_no_permission_title))
                },
                text = {
                    Text(stringResource(Res.string.push_no_permission_body))
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val intent = Intent()
                            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                            intent.putExtra(
                                "android.provider.extra.APP_PACKAGE",
                                context.packageName
                            )
                            openPermissionSettings.launch(intent)
                            permissionAlertDialogShow = false
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.push_no_permission_fix)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            permissionAlertDialogShow = false
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.push_no_permission_later)
                        )
                    }
                }
            )
        }
    }
}