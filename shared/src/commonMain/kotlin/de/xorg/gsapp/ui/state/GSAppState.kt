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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.model.SubstitutionDisplay

class GSAppState {
    var substitutionState by mutableStateOf(UiState.EMPTY)
    var substitutionsList: SnapshotStateList<SubstitutionDisplay> = mutableStateListOf()
    var substitutionsNotes by mutableStateOf("")
    var substitutionsDate by mutableStateOf("")
    var filterRole by mutableStateOf(FilterRole.ALL)
    var filter by mutableStateOf("")

    var foodplanState by mutableStateOf(UiState.LOADING)
    var foodplanList: SnapshotStateList<FoodOffer> = mutableStateListOf()

    var subjectsList: SnapshotStateList<Subject> = mutableStateListOf()

    var teachersList: SnapshotStateList<Teacher> = mutableStateListOf()
}