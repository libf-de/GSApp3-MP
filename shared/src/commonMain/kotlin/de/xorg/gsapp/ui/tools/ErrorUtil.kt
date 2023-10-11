/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023 Fabian Schillig
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.xorg.gsapp.ui.tools

import androidx.compose.runtime.Composable
import de.xorg.gsapp.res.MR
import dev.icerock.moko.resources.compose.stringResource

/**
 * Helper functions to work with localized dates.
 */
@Composable
fun getErrorAsString(throwable: Throwable): String {
    return stringResource(
        resource = MR.strings.foodplan_error,
        throwable.message ?: stringResource(MR.strings.generic_error_null)
    )
}