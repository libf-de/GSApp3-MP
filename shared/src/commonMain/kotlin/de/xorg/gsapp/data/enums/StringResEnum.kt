/*
 * This file was cherry-picked from MensaApp (https://github.com/mensa-app-wuerzburg/Android)
 * Used with permission.
 * Copyright (C) 2023 Erik Spall
 *               2023 Fabian Schillig
 */

package de.xorg.gsapp.data.enums

import de.xorg.gsapp.res.MR
import de.xorg.gsapp.ui.state.FilterRole
import de.xorg.gsapp.ui.state.PushState
import dev.icerock.moko.resources.StringResource

interface StringResEnum {

    fun getValue(): StringResource

    fun getRawValue(): String

    companion object {
        fun roleFrom(stringRes: StringResource): FilterRole {
            return when (stringRes) {
                MR.strings.role_all -> FilterRole.ALL
                MR.strings.role_student -> FilterRole.STUDENT
                MR.strings.role_teacher -> FilterRole.TEACHER
                else -> FilterRole.ALL
            }
        }

        fun locationFrom(stringRes: StringResource): PushState {
            return when (stringRes) {
                MR.strings.push_disabled -> PushState.DISABLED
                MR.strings.push_filter -> PushState.LIKE_FILTER
                MR.strings.push_enabled -> PushState.ENABLED
                else -> PushState.DISABLED

            }
        }
    }
}