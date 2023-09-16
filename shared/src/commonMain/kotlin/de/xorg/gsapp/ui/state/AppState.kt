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

package de.xorg.gsapp.ui.state

import de.xorg.gsapp.data.exceptions.NoException
import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AppState(
    val currentView: ViewState = ViewState.SUBSTITUTIONS,

    val substitutionsFlow: StateFlow<SubstitutionSet> = MutableStateFlow(
        SubstitutionSet("", "", emptyMap())),
    val substitutionState: UiState = UiState.EMPTY,
    val substitutionError: Throwable = NoException(),
    @Deprecated("use flow instead") val substitutionList: List<Substitution> = listOf(),
    @Deprecated("use flow instead") val substitutionDate: String = "",
    @Deprecated("use flow instead") val substitutionNotes: String = "",

    val filterRole: FilterRole = FilterRole.ALL,
    val filter: String = "",


    val foodplanState: UiState = UiState.EMPTY,
    val foodplanList: List<FoodOffer> = listOf(),
    val foodplanError: Throwable = NoException(),


    val subjectsList: List<Subject> = listOf(),


    val teachersList: List<Teacher> = listOf()
)
