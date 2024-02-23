package de.xorg.gsapp.ui.tools

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

fun MakeToast(context: Context, message: StringResource, duration: Int = Toast.LENGTH_SHORT) {
    CoroutineScope(Dispatchers.IO).launch {
        Toast.makeText(context, getString(message), duration).show()
    }
}