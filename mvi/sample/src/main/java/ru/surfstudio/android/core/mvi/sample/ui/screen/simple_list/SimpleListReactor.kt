package ru.surfstudio.android.core.mvi.sample.ui.screen.simple_list

import ru.surfstudio.android.dagger.scope.PerScreen
import javax.inject.Inject
import ru.surfstudio.android.core.mvi.sample.ui.screen.simple_list.SimpleListEvent.*
import ru.surfstudio.android.core.mvi.sample.ui.screen.simple_list.controller.StepperData
import ru.surfstudio.android.core.mvi.ui.reducer.Reducer
import ru.surfstudio.android.logger.Logger

/**
 * Хранитель состояния экрана [SimpleListActivityView]
 */
data class SimpleListModel(
        val items: List<StepperData> = listOf()
)

/**
 * Реактор экрана [SimpleListActivityView]
 */
@PerScreen
class SimpleListReactor @Inject constructor() : Reducer<SimpleListEvent, SimpleListModel> {

    override fun reduce(state: SimpleListModel, event: SimpleListEvent): SimpleListModel {
        Logger.d("reduce state on $event")
        return when (event) {
            is ListLoaded -> onListLoaded(state, event)
            is StepperClicked -> onStepperClicked(state, event)
            else -> state
        }
    }

    private fun onStepperClicked(state: SimpleListModel, event: StepperClicked): SimpleListModel =
            state.copy(items = state.items.map {
                if (it.id == event.id) it.copy(clicks = it.clicks + 1) else it
            })

    private fun onListLoaded(state: SimpleListModel, event: ListLoaded): SimpleListModel =
            state.copy(items = event.list)
}