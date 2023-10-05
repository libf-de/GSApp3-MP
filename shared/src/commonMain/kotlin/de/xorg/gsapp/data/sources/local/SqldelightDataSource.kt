package de.xorg.gsapp.data.sources.local

import de.xorg.gsapp.data.model.Additive
import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.data.model.Subject
import de.xorg.gsapp.data.model.SubstitutionApiModelSet
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.data.model.Teacher
import de.xorg.gsapp.data.sql.GsAppDatabase
import kotlinx.datetime.LocalDate
import org.kodein.di.DI
import org.kodein.di.instance

class SqldelightDataSource(di: DI) : LocalDataSource {

    val database: GsAppDatabase by di.instance()

    override suspend fun loadSubstitutionPlan(): Result<SubstitutionSet> {
        TODO("Not yet implemented")
    }

    override suspend fun storeSubstitutionPlan(value: SubstitutionSet) {
        TODO("Not yet implemented")
    }

    override suspend fun loadSubjects(): Result<List<Subject>> {
        TODO("Not yet implemented")
    }

    override suspend fun storeSubjects(value: List<Subject>) {
        TODO("Not yet implemented")
    }

    override suspend fun loadTeachers(): Result<List<Teacher>> {
        TODO("Not yet implemented")
    }

    override suspend fun storeTeachers(value: List<Teacher>) {
        TODO("Not yet implemented")
    }

    override suspend fun loadFoodPlan(): Result<Map<LocalDate, List<Food>>> {
        TODO("Not yet implemented")
    }

    override suspend fun storeFoodPlan(value: Map<LocalDate, List<Food>>) {
        TODO("Not yet implemented")
    }

    override suspend fun loadAdditives(): Result<List<Additive>> {
        TODO("Not yet implemented")
    }

    override suspend fun storeAdditives(value: List<Additive>) {
        TODO("Not yet implemented")
    }

}