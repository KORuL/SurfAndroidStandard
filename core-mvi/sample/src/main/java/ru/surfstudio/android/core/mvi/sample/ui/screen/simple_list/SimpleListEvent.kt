package ru.surfstudio.android.core.mvi.sample.ui.screen.simple_list

import ru.surfstudio.android.core.mvi.event.Event
import ru.surfstudio.android.core.mvi.event.lifecycle.LifecycleEvent
import ru.surfstudio.android.core.ui.state.LifecycleStage

/**
 * События экрана [SimpleListActivityView]
 */
sealed class SimpleListEvent : Event {
    data class StepperClicked(val position: Int) : SimpleListEvent()
    data class ListLoaded(val list: List<Int>) : SimpleListEvent()
    data class SimpleListLifecycle(override var stage: LifecycleStage) : SimpleListEvent(), LifecycleEvent
}