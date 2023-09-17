package de.xorg.gsapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingComponent(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier,
           verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator()
        Text("Wird geladen…")
    }

}