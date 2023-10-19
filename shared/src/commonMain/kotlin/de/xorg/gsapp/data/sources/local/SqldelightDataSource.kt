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

import androidx.compose.ui.graphics.Color
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.exceptions.ElementNotFoundException
import de.xorg.gsapp.data.exceptions.EmptyStoreException
import de.xorg.gsapp.data.exceptions.NoEntriesException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.`sources-legacy`.local.LocalDataSource
import de.xorg.gsapp.data.sql.GsAppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.kodein.di.DI
import org.kodein.di.instance
import org.lighthousegames.logging.logging


/**
 * This uses a single SQLDelight database for local storage:
 * https://github.com/cashapp/sqldelight
 * Will probably be removed in favour of database storage.
 *
 * + IT'S A DATABASE, sometimes faster than JSON, will probably be faster when implemented properly
 * - cache data not stored in platform-specific cache, so won't be cleared automatically if low on
 *   disk space (if supported by platform)
 */

class SqldelightDataSource(di: DI) : LocalDataSource {

    companion object {
        val log = logging()
    }

    private val database: GsAppDatabase by di.instance()

    private fun <T> tryFlow(flowToTry: Flow<Result<T>>): Flow<Result<T>> = try {
        flowToTry
    } catch (ex: Exception) {
        flow {emit(Result.failure(ex)) }
    }

    override suspend fun loadSubstitutionPlan(): Result<SubstitutionSet> {
        return try {
            val latest = database.dbSubstitutionSetQueries.selectLatest().executeAsOneOrNull()
                ?: return Result.failure(EmptyStoreException())
            val substitutions: Map<String, List<Substitution>> = database.dbSubstitutionQueries
                .findSubstitutionsBySetId(latest.id)
                .executeAsList()
                .map {
                    Substitution(
                        klass = it.klass,
                        lessonNr = it.lessonNr ?: "?",
                        origSubject = Subject(
                            shortName = it.origShortName ?: "??",
                            longName = it.origLongName ?: "??",
                            color = it.origColor ?: Color.Magenta
                        ),
                        substTeacher = Teacher(
                            shortName = it.substTeacherShortName ?: "??",
                            longName = it.substTeacherLongName ?: "??"
                        ),
                        substRoom = it.substRoom ?: "??",
                        substSubject = Subject(
                            shortName = it.substShortName ?: "??",
                            longName = it.substLongName ?: "??",
                            color = it.substColor ?: Color.Magenta
                        ),
                        notes = it.notes ?: "",
                        isNew = it.isNew
                    )
                }
                .groupBy { subs -> subs.klass }
            Result.success(
                SubstitutionSet(
                    dateStr = latest.dateStr,
                    date = latest.date,
                    notes = latest.notes ?: "",
                    substitutions = substitutions
                )
            )
        } catch(ex: Exception) {
            log.e { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }

    override fun getSubstitutionPlanFlow(): Flow<Result<SubstitutionSet>> =
        tryFlow(
            database.dbSubstitutionSetQueries
                .selectLatest()
                .asFlow()
                .mapToOneOrNull(Dispatchers.IO)
                .flatMapLatest { dbSubset ->
                    if(dbSubset == null) //Return exception if there is no latest SubstitutionSet
                        return@flatMapLatest flow {
                            emit(
                                Result.failure(NoEntriesException())
                            )
                        }

                    database.dbSubstitutionQueries
                        .findSubstitutionsBySetId(dbSubset.id)
                        .asFlow()
                        .mapToList(Dispatchers.IO)
                        .map {
                            Result.success(
                                SubstitutionSet(
                                    dateStr = dbSubset.dateStr,
                                    date = dbSubset.date,
                                    notes = dbSubset.notes ?: "",
                                    substitutions = it.map { dbSub ->
                                        Substitution(
                                            klass = dbSub.klass,
                                            lessonNr = dbSub.lessonNr ?: "?",
                                            origSubject = Subject(
                                                shortName = dbSub.origShortName ?: "??",
                                                longName = dbSub.origLongName ?: "??",
                                                color = dbSub.origColor ?: Color.Magenta
                                            ),
                                            substTeacher = Teacher(
                                                shortName = dbSub.substTeacherShortName ?: "??",
                                                longName = dbSub.substTeacherLongName ?: "??"
                                            ),
                                            substRoom = dbSub.substRoom ?: "??",
                                            substSubject = Subject(
                                                shortName = dbSub.substShortName ?: "??",
                                                longName = dbSub.substLongName ?: "??",
                                                color = dbSub.substColor ?: Color.Magenta
                                            ),
                                            notes = dbSub.notes ?: "",
                                            isNew = dbSub.isNew
                                        )
                                    }.groupBy { subs -> subs.klass }
                                )
                            )
                        }
                }
        )

    override suspend fun addSubstitutionPlan(value: SubstitutionSet) {
        val subsList = value.substitutions.flatMap { it.value }.distinct()
        val teachers = subsList.map { it.substTeacher }.distinct()
        val subjects = subsList.flatMap { listOf(it.origSubject, it.substSubject) }.distinct()

        database.transaction {
            // Store missing teachers in the database
            teachers.forEach { teacher ->
                database.dbTeacherQueries.insertTeacher(
                    shortName = teacher.shortName,
                    longName = teacher.longName
                )
            }

            // Store missing subjects in the database
            /*subjects.forEach { subject ->
                database.dbSubjectQueries.insertSubject(
                    shortName = subject.shortName,
                    longName = subject.longName,
                    color = subject.color
                )
            }*/

            database.dbSubstitutionSetQueries.insertSubstitutionSet(
                date = value.date,
                dateStr = value.dateStr,
                notes = value.notes
            )

            val setId = database.dbSubstitutionSetQueries.lastInsertRowId().executeAsOne()

            subsList.forEach {sub ->
                database.dbSubstitutionQueries.insertSubstitution(
                    assSet = setId,
                    klass = sub.klass,
                    lessonNr = sub.lessonNr,
                    origSubject = sub.origSubject.shortName,
                    substTeacher = sub.substTeacher.shortName,
                    substRoom = sub.substRoom,
                    substSubject = sub.substSubject.shortName,
                    notes = sub.notes,
                    isNew = sub.isNew
                )
            }
        }
    }

    override suspend fun cleanupSubstitutionPlan() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val oldSets = database.dbSubstitutionSetQueries.getLegacyIds(today).executeAsList()
        log.d { "cleanupSubstitutionPlan(): Found ${oldSets.size} old plans..." }

        database.dbSubstitutionQueries.transaction {
            oldSets.forEach { setId -> database.dbSubstitutionQueries.deleteBySetId(setId) }
        }

        database.dbSubstitutionSetQueries.transaction {
            oldSets.forEach { setId ->
                database.dbSubstitutionSetQueries.deleteSubstitutionSet(setId)
            }
        }

        log.d { "cleanupSubstitutionPlan(): Cleanup done!" }
    }

    override fun getSubjectsFlow(): Flow<Result<List<Subject>>>
        = tryFlow(database.dbSubjectQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                Result.success(
                    it.map { dbSub ->
                        Subject(dbSub)
                    }
                )
            })

    override suspend fun addAllSubjects(value: List<Subject>) {
        database.dbSubjectQueries.transaction {
            value.forEach { subject ->
                database.dbSubjectQueries.insertSubject(
                    shortName = subject.shortName,
                    longName = subject.longName,
                    color = subject.color
                )
            }
        }
    }

    override suspend fun addSubject(value: Subject) {
        database.dbSubjectQueries.insertSubject(
            shortName = value.shortName,
            longName = value.longName,
            color = value.color
        )
    }

    override suspend fun subjectExists(shortName: String): Boolean {
        return try {
            database.dbSubjectQueries.countByShort(shortName).executeAsOne() > 0
        } catch(ex: Exception) {
            log.w { "Exception while checking subject existence: ${ex.stackTraceToString()}"}
            false
        }
    }

    override fun getTeachersFlow(): Flow<Result<List<Teacher>>>
        = database.dbTeacherQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                Result.success(
                    it.map { dbTeacher -> Teacher(dbTeacher) }
                )
            }

    override suspend fun addAllTeachers(value: List<Teacher>) {
        database.dbTeacherQueries.transaction {
            value.forEach {teacher ->
                database.dbTeacherQueries.insertTeacher(
                    shortName = teacher.shortName,
                    longName = teacher.longName
                )
            }
        }
    }

    override suspend fun addTeacher(value: Teacher) {
        database.dbTeacherQueries.insertTeacher(
            shortName = value.shortName,
            longName = value.longName
        )
    }

    override suspend fun updateTeacher(value: Teacher) {
        database.dbTeacherQueries.updateTeacher(
            shortName = value.shortName,
            longName = value.longName
        )
    }

    override suspend fun deleteTeacher(value: Teacher) {
        database.dbTeacherQueries.deleteTeacher(
            shortName = value.shortName
        )
    }

    override fun getFoodMapFlow(): Flow<Result<Map<LocalDate, List<Food>>>> =
        database.dbFoodQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbFoodList ->
                Result.success(
                    dbFoodList.groupBy { it.date }
                      .mapValues { foodList ->
                        foodList.value.map { dbFood ->
                            Food(
                                num = dbFood.foodId.toInt(),
                                name = dbFood.name,
                                additives = dbFood.additives
                            )
                        }
                      }
                )
            }

    override fun getFoodsForDateFlow(date: LocalDate): Flow<Result<List<Food>>>
        = database.dbFoodQueries.selectByDate(date).asFlow().mapToList(Dispatchers.IO).map {
            Result.success(
                it.map { dbFood ->
                    Food(
                        num = dbFood.foodId.toInt(),
                        name = dbFood.name,
                        additives = dbFood.additives
                    )
                }
            )
        }

    override fun getFoodDatesFlow(): Flow<Result<List<LocalDate>>>
        = try {
            database.dbFoodQueries
                .selectAllDates()
                .asFlow()
                .mapToList(Dispatchers.IO)
                .map {
                    Result.success(it)
                }
        } catch(ex: Exception) {
            flow { emit(Result.failure(ex)) }
        }

    override suspend fun addFood(value: Food) {
        TODO("Not yet implemented")
    }

    override suspend fun addAllFoods(value: List<Food>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateFood(value: Food) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFood(value: Food) {
        TODO("Not yet implemented")
    }

    override suspend fun storeSubjects(value: List<Subject>) {

    }

    override suspend fun storeSubject(subject: Subject) {

    }

    override suspend fun updateSubject(subject: Subject) {
        database.dbSubjectQueries.updateSubject(
            shortName = subject.shortName,
            longName = subject.longName,
            color = subject.color
        )
    }

    override suspend fun deleteSubject(value: Subject) {
        database.dbSubjectQueries.deleteSubject(value.shortName)
    }

    override suspend fun loadTeachers(): Result<List<Teacher>> {
        return try {
            Result.success(database.dbTeacherQueries.selectAll().executeAsList().map {
                Teacher(shortName = it.shortName, longName = it.longName)
            })
        } catch (ex: Exception) {
            log.e { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }

    override suspend fun storeTeachers(value: List<Teacher>) {

    }

    override suspend fun loadFoodPlan(): Result<Map<LocalDate, List<Food>>> {
        return try {
            Result.success(
                database.dbFoodQueries
                    .selectAll()
                    .executeAsList()
                    .groupBy { it.date }
                    .mapValues { foodList ->
                        foodList.value.map { dbFood ->
                            Food(
                                num = dbFood.foodId.toInt(),
                                name = dbFood.name,
                                additives = dbFood.additives
                            )
                        }
                    }
            )
        } catch (ex: Exception) {
            log.e { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }

    override suspend fun storeFoodPlan(value: Map<LocalDate, List<Food>>) {
        database.dbFoodQueries.transaction {
            value.forEach { dayFoods ->
                dayFoods.value.forEach { food ->
                    database.dbFoodQueries.insert(
                        date = dayFoods.key,
                        foodId = food.num.toLong(),
                        name = food.name,
                        additives = food.additives
                    )
                }
            }
        }
    }

    override suspend fun cleanupFoodPlan() {
        val sevenDaysAgo = Clock.System
            .todayIn(TimeZone.currentSystemDefault())
            .minus(7, DateTimeUnit.DAY)
        database.dbFoodQueries.clearOld(sevenDaysAgo)
    }

    override fun getAdditivesFlow(): Flow<Result<Map<String, String>>> {
        TODO("Not yet implemented")
    }

    override suspend fun loadAdditives(): Result<Map<String, String>> {
        return try {
            Result.success(
                database.dbAdditiveQueries.selectAll().executeAsList().associate {
                    it.shortName to it.longName
                }
            )
        } catch(ex: Exception) {
            log.e { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }

    override suspend fun storeAdditives(value: Map<String, String>) {
        database.dbAdditiveQueries.transaction {
            value.forEach {
                database.dbAdditiveQueries.insert(shortName = it.key, longName = it.value)
            }
        }
    }

    override fun getExamsFlow(): Flow<Result<Map<LocalDate, List<Exam>>>> {
        TODO("Not yet implemented")
    }

    override suspend fun loadExams(course: ExamCourse): Result<Map<LocalDate, List<Exam>>> {
        return try {
            Result.success(
                database.dbExamQueries.selectByCourseWithSubjects(course).executeAsList().map {
                    val subject = if(it.subject == null ||
                                  it.subjectLongName == null ||
                                  it.subjectColor == null)
                                    null
                               else
                                    Subject(
                                        shortName = it.subject,
                                        longName = it.subjectLongName,
                                        color = it.subjectColor
                                    )
                    Exam(label = it.label,
                         date = it.date,
                         course = it.course,
                         isCoursework = it.isCoursework,
                         subject = subject)
                }.groupBy { it.date }
            )
        } catch(ex: Exception) {
            log.e { ex.stackTraceToString() }
            Result.failure(ex)
        }
    }

    override suspend fun storeExams(value: Map<LocalDate, List<Exam>>) {
        database.dbExamQueries.transaction {
            value.flatMap { it.value }.forEach {
                database.dbExamQueries.insert(
                    label = it.label,
                    date = it.date,
                    course = it.course,
                    isCoursework = it.isCoursework,
                    subject = it.subject?.shortName
                )
            }
        }
    }

    override suspend fun cleanupExams() {
        val aMonthAgo = Clock.System
            .todayIn(TimeZone.currentSystemDefault())
            .minus(1, DateTimeUnit.MONTH)
        database.dbExamQueries.clearOlder(aMonthAgo)
    }

    override suspend fun deleteExam(toDelete: Exam) {
        database.dbExamQueries.deleteByValues(toDelete.label, toDelete.date)
    }

}