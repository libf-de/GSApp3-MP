package de.xorg.gsapp.ui.viewmodels

import de.xorg.gsapp.data.model.Food
import de.xorg.gsapp.ui.state.ComponentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

class FoodplanViewModel : GSAppViewModel() {
    private val _foodplanState =
        MutableStateFlow<ComponentState<Map<LocalDate, List<Food>>>>(ComponentState.EmptyLocal)
    val componentState: StateFlow<ComponentState<Map<LocalDate, List<Food>>>> =
        _foodplanState
    private val foodplanFlow = appRepo.getFoodplan()

    init {
        initState(foodplanFlow, _foodplanState)
        refresh()
    }

    fun refresh() = refresh(
        refreshFunction = appRepo::updateFoodplan,
        targetState = _foodplanState,
        flowToRecoverFrom = foodplanFlow,
    )
}