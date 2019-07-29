package ru.surfstudio.android.core.mvi.event.lifecycle

import ru.surfstudio.android.core.mvi.event.Event

/**
 * Событие изменение жизненного цикла экрана
 */
interface LifecycleEvent : Event {
    var stage: LifecycleStage
}