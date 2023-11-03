/*
 * GSApp3 (https://github.com/libf-de/GSApp3)
 * Copyright (C) 2023. Fabian Schillig
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
package de.xorg.gsapp.ui.materialtools.annotations

/**
 * Denotes that the annotated element should be a float or double in the given range
 *
 *
 * Example:
 * ```
 * @FloatRange(from=0.0,to=1.0)
 * public float getAlpha() {
 *     ...
 * }
 * ```
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.ANNOTATION_CLASS
)
annotation class FloatRange(
    /**
     * Smallest value. Whether it is inclusive or not is determined
     * by [.fromInclusive]
     */
    val from: Double = Double.NEGATIVE_INFINITY,
    /**
     * Largest value. Whether it is inclusive or not is determined
     * by [.toInclusive]
     */
    val to: Double = Double.POSITIVE_INFINITY,
    /** Whether the from value is included in the range  */
    val fromInclusive: Boolean = true,
    /** Whether the to value is included in the range  */
    val toInclusive: Boolean = true
)