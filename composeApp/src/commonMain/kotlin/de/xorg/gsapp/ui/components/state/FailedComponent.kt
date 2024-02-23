package de.xorg.gsapp.ui.components.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.xorg.gsapp.ui.tools.PlatformInterface
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import gsapp.composeapp.generated.resources.Res
import gsapp.composeapp.generated.resources.failed_send_dev
import gsapp.composeapp.generated.resources.failed_to_load
import gsapp.composeapp.generated.resources.unknown_cause
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.koinInject

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FailedComponent(
    exception: Throwable,
    where: StringResource,
    modifier: Modifier = Modifier
) {
    FailedComponent(exception, stringResource(where), modifier)
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FailedComponent(
    exception: Throwable,
    where: String,
    modifier: Modifier = Modifier
) {
    val platformInterface: PlatformInterface = koinInject()

    Column(modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(Res.string.failed_to_load, where),
            style = MaterialTheme.typography.titleMedium
        )
        Text(exception.message ?: stringResource(Res.string.unknown_cause))
        Text(stringResource(Res.string.failed_send_dev))

        platformInterface.SendErrorReportButton(exception)
    }

}