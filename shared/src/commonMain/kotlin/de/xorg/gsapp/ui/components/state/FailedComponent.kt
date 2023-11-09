package de.xorg.gsapp.ui.components.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.xorg.gsapp.ui.tools.PlatformInterface
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject

@Composable
fun FailedComponent(
    exception: Throwable,
    where: StringResource,
    modifier: Modifier = Modifier
) {
    FailedComponent(exception, stringResource(where), modifier)
}

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
            text = stringResource(MR.strings.failed_to_load, where),
            style = MaterialTheme.typography.titleMedium
        )
        Text(exception.message ?: stringResource(MR.strings.unknown_cause))
        Text(stringResource(MR.strings.failed_send_dev))

        platformInterface.SendErrorReportButton(exception)
    }

}