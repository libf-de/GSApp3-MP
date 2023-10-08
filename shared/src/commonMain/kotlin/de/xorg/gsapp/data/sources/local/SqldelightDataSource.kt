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
import de.xorg.gsapp.data.DbFood
import de.xorg.gsapp.data.enums.ExamCourse
import de.xorg.gsapp.data.exceptions.EmptyStoreException
import de.xorg.gsapp.data.model.Exam
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.Substitution
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.sql.GsAppDatabase
import kotlinx.datetime.LocalDate
import org.kodein.di.DI
import org.kodein.di.instance


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

    val database: GsAppDatabase by di.instance()

    /*
    klass: String,
        lessonNr: String,
        origSubject: Subject,
        substTeacher: Teacher,
        substRoom: String,
        substSubject: Subject,
        notes: String,
        isNew: Boolean
     */
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
                    date = latest.date,
                    notes = latest.notes ?: "",
                    substitutions = substitutions
                )
            )
        } catch(ex: Exception) {
            ex.printStackTrace()
            Result.failure(ex);
        }
    }

    override suspend fun storeSubstitutionPlan(value: SubstitutionSet) {
        val subsList = value.substitutions.flatMap { it.value }.distinct()
        val teachers = subsList.map { it.substTeacher }.distinct()
        val subjects = subsList.flatMap { listOf(it.origSubject, it.substSubject) }.distinct()

        database.transaction {
            teachers.forEach { teacher ->
                database.dbTeacherQueries.insertTeacher(
                    shortName = teacher.shortName,
                    longName = teacher.longName
                )
            }

            subjects.forEach { subject ->
                database.dbSubjectQueries.insertSubject(
                    shortName = subject.shortName,
                    longName = subject.longName,
                    color = subject.color
                )
            }

            database.dbSubstitutionSetQueries.insertSubstitutionSet(
                date = value.date,
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

    override suspend fun loadSubjects(): Result<List<Subject>> {
        return try {
            Result.success(database.dbSubjectQueries.selectAll().executeAsList().map {
                Subject(shortName = it.shortName, longName = it.longName, color = it.color ?: Color.Magenta)
            })
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun storeSubjects(value: List<Subject>) {
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

    override suspend fun loadTeachers(): Result<List<Teacher>> {
        return try {
            Result.success(database.dbTeacherQueries.selectAll().executeAsList().map {
                Teacher(shortName = it.shortName, longName = it.longName)
            })
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    override suspend fun storeTeachers(value: List<Teacher>) {
        database.dbTeacherQueries.transaction {
            value.forEach {teacher ->
                database.dbTeacherQueries.insertTeacher(
                    shortName = teacher.shortName,
                    longName = teacher.longName
                )
            }
        }
    }

    override suspend fun loadFoodPlan(): Result<Map<LocalDate, List<Food>>> {
        return try {
            Result.success(
                database.dbFoodQueries.selectAll().executeAsList().groupBy<DbFood, LocalDate> {
                    it.date
                }.mapValues { foodList ->
                    foodList.value.map { dbFood ->
                        Food(
                            num = dbFood.foodId.toInt(),
                            name = dbFood.name,
                            additives = dbFood.additives
                        )
                    }
                })
        } catch (ex: Exception) {
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

    override suspend fun loadAdditives(): Result<Map<String, String>> {
        return try {
            Result.success(
                database.dbAdditiveQueries.selectAll().executeAsList().map {
                    it.shortName to it.longName
                }.toMap()
            )
        } catch(ex: Exception) {
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

}