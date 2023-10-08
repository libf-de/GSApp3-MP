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
package de.xorg.gsapp.ui.materialtools

import de.xorg.gsapp.ui.materialtools.annotations.ColorInt

/**
 * Each accent color (primary, secondary and tertiary), is provided as a group of four supplementary
 * color roles with different luminance which can be used in the UI to define emphasis and to
 * provide a greater flexibility in expression.
 */
class ColorRoles internal constructor(
    /** Returns the accent color, used as the main color from the color role.  */
    @get:ColorInt
    @param:ColorInt val accent: Int,
    /**
     * Returns the on_accent color, used for content such as icons and text on top of the Accent
     * color.
     */
    @get:ColorInt
    @param:ColorInt val onAccent: Int,
    /** Returns the accent_container color, used with less emphasis than the accent color.  */
    @get:ColorInt
    @param:ColorInt val accentContainer: Int,
    /**
     * Returns the on_accent_container color, used for content such as icons and text on top of the
     * accent_container color.
     */
    @get:ColorInt
    @param:ColorInt val onAccentContainer: Int
)