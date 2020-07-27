/*
  Copyright (c) 2020, SurfStudio LLC.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package ru.surfstudio.android.core.mvi.util

import io.reactivex.Observable
import ru.surfstudio.android.core.mvi.event.hub.RxEventHub

/**
 * Фильтрация Observable по типу [R].
 *
 */
inline fun <reified R> Observable<*>.filterIsInstance(): Observable<R> = this
        .ofType(R::class.java)

/**
 * Фильтрация событий из RxEventHub по типу [R].
 */
inline fun <reified R> RxEventHub<*>.observeOnly(): Observable<R> = observe()
        .ofType(R::class.java)