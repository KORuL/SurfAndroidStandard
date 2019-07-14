package ru.surfstudio.android.core.mvi.util

import io.reactivex.Observable
import ru.surfstudio.android.core.mvi.event.hub.RxEventHub

/**
 * Фильтрация Observable по типу [R].
 *
 */
inline fun <reified R> Observable<*>.filterIsInstance(): Observable<R> = this
        .filter { it is R }
        .map { it as R }

/**
 * Фильтрация событий из RxEventHub по типу [R].
 */
inline fun <reified R> RxEventHub<*>.observeOnly(): Observable<R> = observe()
        .filterIsInstance()