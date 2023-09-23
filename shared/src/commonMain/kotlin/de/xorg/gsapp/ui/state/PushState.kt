package de.xorg.gsapp.ui.state

import de.xorg.gsapp.data.enums.StringResEnum
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.StringResource

enum class PushState(val value: Int) : StringResEnum {
    DISABLED(0) { override val labelResource: StringResource = MR.strings.push_disabled },
    LIKE_FILTER(1) { override val labelResource: StringResource = MR.strings.push_filter },
    ENABLED(2) { override val labelResource: StringResource = MR.strings.push_enabled };

    companion object {
        fun fromInt(value: Int): PushState
                = PushState.values().firstOrNull { it.value == value } ?: DISABLED
    }
}