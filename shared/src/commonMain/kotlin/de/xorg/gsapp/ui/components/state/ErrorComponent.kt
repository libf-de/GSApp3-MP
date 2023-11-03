package de.xorg.gsapp.ui.components.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import de.xorg.gsapp.data.platform.PlatformInterface
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject

@Composable
fun ErrorComponent(
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
        Text(exception.message ?: "unbekannte Ursache")
        Text(stringResource(MR.strings.failed_send_dev))

        platformInterface.sendErrorReport(exception)

        /*Button(
            onClick = { platformInterface.sendErrorReport(exception) }
        ) {
            Text("Send mail")
        }*/
    }

}