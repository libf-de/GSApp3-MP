package de.xorg.gsapp.ui.viewmodels

import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.ui.state.ComponentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

class FoodplanViewModel : GSAppViewModel() {
    private val _foodplanState =
        MutableStateFlow<ComponentState<Map<LocalDate, List<Food>>, Throwable>>(ComponentState.EmptyLocal)
    val foodplanState: StateFlow<ComponentState<Map<LocalDate, List<Food>>, Throwable>> =
        _foodplanState
    private val foodplanFlow = appRepo.getFoodplan()

    init {
        initState(foodplanFlow, _foodplanState)
    }

    fun updateFoodplan() = refresh(
        refreshFunction = appRepo::updateFoodplan,
        targetState = _foodplanState,
        flowToRecoverFrom = foodplanFlow,
    )
}