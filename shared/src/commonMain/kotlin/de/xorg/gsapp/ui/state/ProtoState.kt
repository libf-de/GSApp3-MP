package de.xorg.gsapp.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ProtoState {
    var role by mutableStateOf(FilterRole.ALL)
    var filter by mutableStateOf("")
}