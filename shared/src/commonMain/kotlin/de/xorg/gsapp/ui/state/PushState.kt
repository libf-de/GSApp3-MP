package de.xorg.gsapp.ui.state

import de.xorg.gsapp.data.enums.StringResEnum
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.StringResource

enum class PushState(
    private val label: StringResource,
    private val rawValue: String
) : StringResEnum {
    DISABLED(MR.strings.push_disabled, "disabled"),
    LIKE_FILTER(MR.strings.push_filter, "filter"),
    ENABLED(MR.strings.push_enabled, "enabled");

    override fun getValue(): StringResource { return label }
    override fun getRawValue(): String { return rawValue }
}