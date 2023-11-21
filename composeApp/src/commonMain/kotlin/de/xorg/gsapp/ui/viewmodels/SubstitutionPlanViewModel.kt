package de.xorg.gsapp.ui.viewmodels

import de.xorg.gsapp.data.model.Filter
import de.xorg.gsapp.data.model.SubstitutionSet
import de.xorg.gsapp.ui.state.ComponentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

class SubstitutionPlanViewModel : GSAppViewModel() {
    private val _substitutionState =
        MutableStateFlow<ComponentState<SubstitutionSet>>(ComponentState.EmptyLocal)
    val componentState: StateFlow<ComponentState<SubstitutionSet>> =
        _substitutionState
    private val subFlow = combine(
        appRepo.getSubstitutions(),
        prefsRepo.getFilterFlow()
    ) { subs, filter ->
        if (filter.role == Filter.Role.ALL) return@combine subs
        else subs.copy(
                substitutions = subs.substitutions.mapValues { subsPerClass ->
                    subsPerClass.value.filter { aSub ->
                        filter.substitutionMatches(aSub)
                    }
                }.filter { subsPerClass ->
                    subsPerClass.value.isNotEmpty()
                }
            )
    }

    init {
        initState(subFlow, _substitutionState)
        refresh()
    }

    fun refresh() = refresh(
        refreshFunction = appRepo::updateSubstitutions,
        targetState = _substitutionState,
        flowToRecoverFrom = subFlow,
    )
}