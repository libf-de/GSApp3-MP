package de.xorg.gsapp.ui.state

import de.xorg.gsapp.data.enums.StringResEnum
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.StringResource

enum class PushState(val value: Int) : StringResEnum {
    DISABLED(0) {
        override val labelResource: StringResource = MR.strings.push_disabled
        override val descriptiveResource: StringResource = labelResource
    },
    LIKE_FILTER(1) {
        override val labelResource: StringResource = MR.strings.push_filter
        override val descriptiveResource: StringResource = labelResource
    },
    ENABLED(2) {
        override val labelResource: StringResource = MR.strings.push_enabled
        override val descriptiveResource: StringResource = labelResource
    };

    companion object {
        val default = DISABLED

        fun fromInt(value: Int): PushState
                = PushState.values().firstOrNull { it.value == value } ?: default
    }
}