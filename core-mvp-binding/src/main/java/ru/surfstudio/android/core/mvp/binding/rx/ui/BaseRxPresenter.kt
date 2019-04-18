/*
 * Copyright (c) 2019-present, SurfStudio LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.surfstudio.android.core.mvp.binding.rx.ui

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import ru.surfstudio.android.core.mvp.binding.rx.relation.Related
import ru.surfstudio.android.core.mvp.binding.rx.relation.mvp.PRESENTER
import ru.surfstudio.android.core.mvp.presenter.BasePresenter
import ru.surfstudio.android.core.mvp.presenter.BasePresenterDependency

/**
 * Презентер поддерживающий связывание модели и представления.
 * Работет в паре с [BindableRxView]
 */
abstract class BaseRxPresenter(
        basePresenterDependency: BasePresenterDependency
) : BasePresenter<BindableRxView>(basePresenterDependency), Related<PRESENTER> {

    override fun relationEntity() = PRESENTER

    override fun <T> subscribe(observable: Observable<T>,
                               onNext: Consumer<T>,
                               onError: (Throwable) -> Unit): Disposable =
            super.subscribe(observable, { onNext.accept(it) }, { onError(it) })
}