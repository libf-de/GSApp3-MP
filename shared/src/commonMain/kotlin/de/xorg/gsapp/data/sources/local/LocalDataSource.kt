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

package de.xorg.gsapp.data.sources.local

import de.xorg.gsapp.data.model.Additive
import de.xorg.gsapp.data.model.FoodOffer
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher

interface LocalDataSource {
    suspend fun loadSubstitutionPlan(): Result<SubstitutionSet>
    suspend fun storeSubstitutionPlan(value: SubstitutionSet)
    suspend fun loadSubjects(): Result<List<Subject>>
    suspend fun storeSubjects(value: List<Subject>)
    suspend fun loadTeachers(): Result<List<Teacher>>
    suspend fun storeTeachers(value: List<Teacher>)

    suspend fun loadFoodPlan(): Result<List<FoodOffer>>
    suspend fun storeFoodPlan(value: List<FoodOffer>)
    suspend fun loadAdditives(): Result<List<Additive>>
    suspend fun storeAdditives(value: List<Additive>)
}