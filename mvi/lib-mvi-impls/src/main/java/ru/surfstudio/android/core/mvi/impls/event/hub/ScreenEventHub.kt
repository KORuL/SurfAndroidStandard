package ru.surfstudio.android.core.mvi.impls.event.hub

import android.content.Intent
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import ru.surfstudio.android.core.mvi.event.factory.EventFactory
import ru.surfstudio.android.core.mvi.event.factory.ParamlessEventFactory
import ru.surfstudio.android.core.mvi.impls.event.hub.back_pressed.BackPressedEventHub
import ru.surfstudio.android.core.mvi.impls.event.hub.dependency.ScreenEventHubDependency
import ru.surfstudio.android.core.mvi.impls.event.hub.new_intent.NewIntentEventHub
import ru.surfstudio.android.core.mvi.event.Event
import ru.surfstudio.android.core.mvi.event.hub.RxEventHub
import ru.surfstudio.android.core.mvi.event.lifecycle.LifecycleEventHub
import ru.surfstudio.android.core.ui.state.ActivityScreenState
import ru.surfstudio.android.core.ui.state.FragmentScreenState
import ru.surfstudio.android.core.ui.state.LifecycleStage
import ru.surfstudio.android.core.ui.state.ScreenState
import ru.surfstudio.android.logger.Logger
import ru.surfstudio.android.mvp.widget.state.WidgetScreenState

/**
 * Реализация [RxEventHub].
 *
 * @param lifecycleEventCreator создатель [LifecycleEvent]
 * @param backPressedCreator    создатель события BackPressed
 * @param newIntentCreator      создатель события OnNewIntent
 */
class ScreenEventHub<T : Event>(
        dependency: ScreenEventHubDependency,
        override val lifecycleEventCreator: EventFactory<LifecycleStage, T>? = null,
        override val backPressedCreator: ParamlessEventFactory<T>? = null,
        override val newIntentCreator: EventFactory<Intent, T>? = null
) : RxEventHub<T>,
        LifecycleEventHub<T, Observable<T>>,
        NewIntentEventHub<T, Observable<T>>,
        BackPressedEventHub<T, Observable<T>> {

    init {
        dependency.screenEventDelegate.registerDelegate(this)
    }

    override val screenState: ScreenState = dependency.screenState

    private val screenName: String = extractScreenName(screenState)

    private fun extractScreenName(screenState: ScreenState): String {
        return when (screenState) {
            is ActivityScreenState -> {
                screenState.coreActivity.screenName
            }

            is FragmentScreenState -> {
                screenState.coreFragment.screenName
            }

            is WidgetScreenState -> {
                "Widget ${screenState.coreWidget.widgetId}"
            }
            else -> {
                ""
            }
        }
    }

    private val bus = PublishRelay.create<T>()

    override fun accept(t: T?) {
        emit(t ?: return)
    }

    override fun emit(event: T) {
        //Log events
        Logger.d("Event | $screenName | $event")
        bus.accept(event)
    }

    override fun observe(): Observable<T> = bus.hide()
}
