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

package de.xorg.gsapp.data.sources.local

import androidx.compose.ui.graphics.Color
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import de.xorg.gsapp.data.exceptions.NoLocalDataException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.sql.GsAppDatabase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 * This uses a single SQLDelight database for local storage:
 * https://github.com/cashapp/sqldelight
 * Will probably be removed in favour of database storage.
 *
 * + IT'S A DATABASE, sometimes faster than JSON, will probably be faster when implemented properly
 * - cache data not stored in platform-specific cache, so won't be cleared automatically if low on
 *   disk space (if supported by platform)
 */

@OptIn(ExperimentalCoroutinesApi::class)
class SqldelightDataSource : LocalDataSource, KoinComponent {

    private val database: GsAppDatabase by inject()

    /**
     * Tries to execute a flow, returns a flow with a failure result if an exception occurs.
     * @param flowToTry Flow<Result<T>> The flow to try to execute
     * @return Flow<Result<T>> The flow with the result
     */
    private fun <T> tryFlow(flowToTry: Flow<Result<T>>): Flow<Result<T>> = try {
        flowToTry
    } catch (ex: Exception) {
        flow { emit(Result.failure(ex)) }
    }

    /**
     * Returns a flow for the substitution plan from local storage.
     * Will return a NoLocalDataException if no local data is available.
     * @return Flow<Result<SubstitutionSet>>
     */
    override fun getSubstitutionPlanFlow(): Flow<SubstitutionSet> =
        database.dbSubstitutionSetQueries
            .selectLatest()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .distinctUntilChanged()
            .flatMapLatest { dbSubset -> //flatMapLatest?
                println("got dbSubset.id ${dbSubset?.id ?: -1}")

                if (dbSubset == null) //Return exception if there is no latest SubstitutionSet
                    error(NoLocalDataException())
                /*return@flatMapLatest flow {
                    error(NoLocalDataException())
                }*/

                database.dbSubstitutionQueries
                    .findSubstitutionsBySetId(dbSubset.id)
                    .asFlow()
                    .mapToList(Dispatchers.IO)
                    .distinctUntilChanged()
                    .map {
                        var haveUnknownSubs = false
                        var haveUnknownTeas = false

                        SubstitutionSet(
                            dateStr = dbSubset.dateStr,
                            date = dbSubset.date,
                            notes = dbSubset.notes ?: "",
                            substitutions = it.map { dbSub ->
                                val origSubjShort = dbSub.origShortName ?: "!!"
                                val substSubjShort = dbSub.substShortName ?: "!!"
                                val substTeaShort = dbSub.substTeacherShortName ?: "!!"

                                if (dbSub.origLongName == null || dbSub.substLongName == null)
                                    haveUnknownSubs = true

                                if (dbSub.substTeacherLongName == null)
                                    haveUnknownTeas = true

                                Substitution(
                                    klass = dbSub.klass,
                                    klassFilter = dbSub.klassFilter,
                                    lessonNr = dbSub.lessonNr ?: "?",
                                    origSubject = Subject(
                                        shortName = origSubjShort,
                                        longName = dbSub.origLongName ?: origSubjShort,
                                        color = dbSub.origColor ?: Color.Magenta
                                    ),
                                    substTeacher = Teacher(
                                        shortName = substTeaShort,
                                        longName = dbSub.substTeacherLongName ?: substTeaShort
                                    ),
                                    substRoom = dbSub.substRoom ?: "??",
                                    substSubject = Subject(
                                        shortName = substSubjShort,
                                        longName = dbSub.substLongName ?: substSubjShort,
                                        color = dbSub.substColor ?: Color.Magenta
                                    ),
                                    notes = dbSub.notes ?: "",
                                    isNew = dbSub.isNew
                                )
                            }.groupBy { subs -> subs.klass },
                            haveUnknownSubs = haveUnknownSubs,
                            haveUnknownTeachers = haveUnknownTeas
                        )

                    }
            }

    /**
     * Returns the latest SubstitutionSet hash and date from local storage.
     * @return Result<Pair<Int, LocalDate>>
     */
    override fun getLatestSubstitutionHashAndDate(): Result<Pair<Int, LocalDate>> {
        return try {
            val latestSet = database
                .dbSubstitutionSetQueries
                .selectLatest()
                .executeAsOneOrNull()

            Result.success(
                Pair(
                    latestSet?.hashCode?.toInt() ?: -1,
                    latestSet?.date ?: LocalDate.fromEpochDays(0)
                )
            )
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    /**
     * Returns the SubstitutionSet id for a given date string.
     * @param dateStr DateString
     * @return Result<Long?> SubstitutionSet id or null if not found
     */
    override fun findSubstitutionSetIdByDateString(dateStr: String): Result<Long?> {
        return try {
            Result.success(
                database
                    .dbSubstitutionSetQueries
                    .getIdByDateString(dateStr)
                    .executeAsOneOrNull()
            )
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun addSubstitutionPlanAndCleanup(value: SubstitutionApiModelSet) {
        database.transaction {
            database.dbSubstitutionSetQueries.insertSubstitutionSet(
                dateStr = value.dateStr ?: "",
                date = value.date,
                notes = value.notes,
                hashCode = value.hashCode().toLong()
                /*hashCode = 12L*/
            )

            val setId = database.dbSubstitutionSetQueries.lastInsertRowId().executeAsOne()

            value.substitutionApiModels.forEach {
                database.dbSubstitutionQueries.insertSubstitution(
                    assSet = setId,
                    klass = it.klass,
                    klassFilter = it.klassFilter,
                    lessonNr = it.lessonNr,
                    origSubject = it.origSubject,
                    substTeacher = it.substTeacher,
                    substRoom = it.substRoom,
                    substSubject = it.substSubject,
                    notes = it.notes,
                    isNew = it.isNew
                )
            }

            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val oldSets = database.dbSubstitutionSetQueries.getLegacyIds(today).executeAsList()
            Napier.d { "cleanupSubstitutionPlan(): Found ${oldSets.size} old plans..." }

            oldSets.forEach { cSetId -> database.dbSubstitutionQueries.deleteBySetId(cSetId) }
            oldSets.forEach { cSetId ->
                database.dbSubstitutionSetQueries.deleteSubstitutionSet(cSetId)
            }

            Napier.d { "cleanupSubstitutionPlan(): Cleanup done!" }
        }
    }

    /**
     * Adds a new SubstitutionSet to local storage.
     * @param value SubstitutionSet to add
     */
    override suspend fun addSubstitutionPlan(value: SubstitutionApiModelSet) {
        database.transaction {
            database.dbSubstitutionSetQueries.insertSubstitutionSet(
                dateStr = value.dateStr ?: "",
                date = value.date,
                notes = value.notes,
                hashCode = value.hashCode().toLong()
                /*hashCode = 12L*/
            )

            val setId = database.dbSubstitutionSetQueries.lastInsertRowId().executeAsOne()

            value.substitutionApiModels.forEach {
                database.dbSubstitutionQueries.insertSubstitution(
                    assSet = setId,
                    klass = it.klass,
                    klassFilter = it.klassFilter,
                    lessonNr = it.lessonNr,
                    origSubject = it.origSubject,
                    substTeacher = it.substTeacher,
                    substRoom = it.substRoom,
                    substSubject = it.substSubject,
                    notes = it.notes,
                    isNew = it.isNew
                )
            }
        }
    }

    /**
     * Clears past SubstitutionSets from local storage.
     */
    override suspend fun cleanupSubstitutionPlan() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val oldSets = database.dbSubstitutionSetQueries.getLegacyIds(today).executeAsList()
        Napier.d { "cleanupSubstitutionPlan(): Found ${oldSets.size} old plans..." }

        database.dbSubstitutionQueries.transaction {
            oldSets.forEach { setId -> database.dbSubstitutionQueries.deleteBySetId(setId) }
        }

        database.dbSubstitutionSetQueries.transaction {
            oldSets.forEach { setId ->
                database.dbSubstitutionSetQueries.deleteSubstitutionSet(setId)
            }
        }

        Napier.d { "cleanupSubstitutionPlan(): Cleanup done!" }
    }

    /**
     * Returns a flow for the list of subjects from local storage.
     * @return Flow<Result<List<Subject>>> List of subjects
     */
    override fun getSubjectsFlow(): Flow<Result<List<Subject>>> = tryFlow(database.dbSubjectQueries
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

    /**
     * Adds new subjects to local storage.
     * @param value List of subjects
     */
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

    /**
     * Adds a new subject to local storage.
     * @param value Subject to add
     */
    override suspend fun addSubject(value: Subject) {
        database.dbSubjectQueries.insertSubject(
            shortName = value.shortName,
            longName = value.longName,
            color = value.color
        )
    }

    /**
     * Deletes all subjects and adds new ones from/to local storage.
     * @param value List of subjects
     */
    override suspend fun resetSubjects(value: List<Subject>) {
        database.dbSubjectQueries.transaction {
            database.dbSubjectQueries.deleteAllSubjects()
            value.forEach { subject ->
                database.dbSubjectQueries.insertSubject(
                    shortName = subject.shortName,
                    longName = subject.longName,
                    color = subject.color
                )
            }
        }
    }

    /**
     * Returns whether there is a subject with the given shortName
     * @param shortName ShortName to check
     * @return Boolean Whether subject exists
     */
    override suspend fun subjectExists(shortName: String): Boolean {
        return try {
            database.dbSubjectQueries.countByShort(shortName).executeAsOne() > 0
        } catch (ex: Exception) {
            Napier.w { "Exception while checking subject existence: ${ex.stackTraceToString()}" }
            false
        }
    }

    /**
     * Returns the number of subjects in local storage.
     * @return Result<Long> Number of subjects
     */
    override suspend fun countSubjects(): Result<Long> {
        return try {
            Result.success(database.dbSubjectQueries.countAll().executeAsOne())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun getAllTeachersShorts(): Result<List<String>> {
        return try {
            Result.success(database.dbTeacherQueries.selectAllShorts().executeAsList())
        } catch(ex: Exception) {
            Result.failure(ex)
        }
    }


    /**
     * Returns a flow for the list of teachers from local storage.
     * @return Flow<Result<List<Teacher>>> List of teachers
     */
    override fun getTeachersFlow(): Flow<Result<List<Teacher>>> = tryFlow(
        database.dbTeacherQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                Result.success(
                    it.map { dbTeacher -> Teacher(dbTeacher) }
                )
            }
    )

    /**
     * Adds new teachers to local storage.
     * @param value List of teachers
     */
    override suspend fun addAllTeachers(value: List<Teacher>) {
        database.dbTeacherQueries.transaction {
            value.forEach { teacher ->
                database.dbTeacherQueries.insertTeacher(
                    shortName = teacher.shortName,
                    longName = teacher.longName
                )
            }
        }
    }

    /**
     * Adds a new teacher to local storage.
     * @param value Teacher to add
     */
    override suspend fun addTeacher(value: Teacher) {
        database.dbTeacherQueries.insertTeacher(
            shortName = value.shortName,
            longName = value.longName
        )
    }

    /**
     * Edits a teacher in local storage.
     * Teacher shortName must already exist in database.
     * @param value Teacher to edit
     */
    override suspend fun updateTeacher(value: Teacher) {
        database.dbTeacherQueries.updateTeacher(
            shortName = value.shortName,
            longName = value.longName
        )
    }

    /**
     * Deletes a teacher from local storage.
     * @param value Teacher to delete
     */
    override suspend fun deleteTeacher(value: Teacher) {
        database.dbTeacherQueries.deleteTeacher(
            shortName = value.shortName
        )
    }

    /**
     * Returns a flow for the food plan from local storage.
     * Will return a NoLocalDataException if no local data is available.
     * @return Flow<Result<Map<LocalDate, List<Food>>>>
     */
    override fun getFoodMapFlow(): Flow<Map<LocalDate, List<Food>>> =
        database.dbFoodQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbFoodList ->
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
            }

    /**
     * Returns a flow for foods for the given date from local storage.
     * Will return a NoLocalDataException if no local data is available.
     * @param date Date to get foods for
     * @return Flow<Result<List<Food>>> Foods for the given date
     */
    override fun getFoodsForDateFlow(date: LocalDate): Flow<Result<List<Food>>> = tryFlow(
        database.dbFoodQueries.selectByDate(date).asFlow().mapToList(Dispatchers.IO).map {
            if (it.isEmpty()) {
                Result.failure(NoLocalDataException())
            } else {
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
        }
    )

    /**
     * Returns a flow for the list of dates for which food is available from local storage.
     * Will return a NoLocalDataException if no local data is available.
     * @return Flow<Result<List<LocalDate>>> List of dates for which food is available
     */
    override fun getFoodDatesFlow(): Flow<Result<List<LocalDate>>> = try {
        database.dbFoodQueries
            .selectAllDates()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map {
                if (it.isEmpty()) Result.failure(NoLocalDataException())
                else Result.success(it)
            }
    } catch (ex: Exception) {
        flow { emit(Result.failure(ex)) }
    }

    /**
     * Returns the latest food plan from local storage as a list.
     * Will return a NoLocalDataException if no local data is available.
     * @return Result<List<Food>> Latest food plan
     */
    override fun getLatestFoods(): Result<List<Food>> {
        return try {
            val foodList = database
                .dbFoodQueries
                .selectLatestFoods()
                .executeAsList()
                .map {
                    Food(
                        num = it.foodId.toInt(),
                        name = it.name,
                        additives = it.additives
                    )
                }

            if (foodList.isEmpty())
                Result.failure(NoLocalDataException())
            else
                Result.success(foodList)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    /**
     * Returns the latest food plan date from local storage.
     * Will return start of epoch if no local data is available.
     * @return Result<LocalDate> Latest food plan date
     */
    override fun getLatestFoodDate(): Result<LocalDate> {
        return try {
            val latestSet = database
                .dbFoodQueries
                .selectLatestFoods()
                .executeAsList()

            if (latestSet.isEmpty())
                Result.success(LocalDate.fromEpochDays(0))
            else
                Result.success(latestSet.first().date)

        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    /**
     * Adds a new food plan to local storage.
     * @param value Map of date to list of food
     */
    override suspend fun addFoodMap(value: Map<LocalDate, List<Food>>) {
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

    /**
     * Edits a subject in local storage.
     * Subject shortName must already exist in database.
     * @param value Subject to edit
     */
    override suspend fun updateSubject(value: Subject) {
        database.dbSubjectQueries.updateSubject(
            shortName = value.shortName,
            longName = value.longName,
            color = value.color
        )
    }

    /**
     * Deletes a subject from local storage.
     * @param value Subject to delete
     */
    override suspend fun deleteSubject(value: Subject) {
        database.dbSubjectQueries.deleteSubject(value.shortName)
    }

    /**
     * Clears past foods from local storage.
     */
    override suspend fun cleanupFoodPlan() {
        val sevenDaysAgo = Clock.System
            .todayIn(TimeZone.currentSystemDefault())
            .minus(7, DateTimeUnit.DAY)
        database.dbFoodQueries.clearOld(sevenDaysAgo)
    }

    /**
     * Returns a flow for the list of additives from local storage.
     * Will return empty map if no local data is available.
     * @return Flow<Result<Map<String, String>>> List of additives
     */
    override fun getAdditivesFlow(): Flow<Map<String, String>> = database
        .dbAdditiveQueries
        .selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { additiveList ->
            additiveList.associate {
                it.shortName to it.longName
            }
        }

    /**
     * Adds new additives to local storage.
     * @param value Map of additives
     */
    override suspend fun addAllAdditives(value: Map<String, String>) {
        database.dbAdditiveQueries.transaction {
            value.forEach {
                database.dbAdditiveQueries.insert(shortName = it.key, longName = it.value)
            }
        }
    }

    /**
     * Returns a flow for the list of exams from local storage.
     * Will return a NoLocalDataException if no local data is available.
     * @return Flow<Result<List<Exam>>> List of exams
     */
    override fun getExamsFlow(): Flow<List<Exam>> = database.dbExamQueries
            .selectAllWithSubjects()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { examList ->
                examList
                    .map {
                        val subject = if (it.subject == null ||
                            it.subjectLongName == null ||
                            it.subjectColor == null
                        )
                            null
                        else
                            Subject(
                                shortName = it.subject,
                                longName = it.subjectLongName,
                                color = it.subjectColor
                            )

                        Exam(
                            label = it.label,
                            date = it.date,
                            course = it.course,
                            subject = subject,
                            isCoursework = it.isCoursework
                        )
                    }
            }


    /**
     * Returns the latest exams from local storage. Won't emit updates as not a flow!
     * @return Result<List<Exam>> Latest exams
     */
    override suspend fun getAllExams(): Result<List<Exam>> {
        return try {
            Result.success(
                database.dbExamQueries.selectAllWithSubjects().executeAsList().map {
                    val subject = if (it.subject == null ||
                        it.subjectLongName == null ||
                        it.subjectColor == null
                    )
                        null
                    else
                        Subject(
                            shortName = it.subject,
                            longName = it.subjectLongName,
                            color = it.subjectColor
                        )
                    Exam(
                        label = it.label,
                        date = it.date,
                        course = it.course,
                        isCoursework = it.isCoursework,
                        subject = subject
                    )
                }
            )
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    /**
     * Clears all exams and adds new ones from/to local storage.
     * @param value List of exams
     */
    override suspend fun clearAndAddAllExams(value: List<Exam>) {
        database.dbExamQueries.transaction {
            database.dbExamQueries.clearAll()

            value.forEach {
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

    /**
     * Adds new exams to local storage.
     * @param value List of exams
     */
    override suspend fun addAllExams(value: List<Exam>) {
        database.dbExamQueries.transaction {
            value.forEach {
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

    /**
     * Clears past exams from local storage.
     */
    override suspend fun cleanupExams() {
        val aMonthAgo = Clock.System
            .todayIn(TimeZone.currentSystemDefault())
            .minus(1, DateTimeUnit.MONTH)
        database.dbExamQueries.clearOlder(aMonthAgo)
    }

    /**
     * Deletes a given exam from local storage.
     * @param toDelete Exam to delete
     */
    override suspend fun deleteExam(toDelete: Exam) {
        database.dbExamQueries.deleteByValues(toDelete.label, toDelete.date)
    }

}