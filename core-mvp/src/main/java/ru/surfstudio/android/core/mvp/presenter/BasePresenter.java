/*
  Copyright (c) 2018-present, SurfStudio LLC, Maxim Tuev.

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
package ru.surfstudio.android.core.mvp.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agna.ferro.rx.CompletableOperatorFreeze;
import com.agna.ferro.rx.MaybeOperatorFreeze;
import com.agna.ferro.rx.ObservableOperatorFreeze;
import com.agna.ferro.rx.SingleOperatorFreeze;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.observers.LambdaObserver;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import ru.surfstudio.android.connection.ConnectionProvider;
import ru.surfstudio.android.core.mvp.error.ErrorHandler;
import ru.surfstudio.android.core.mvp.view.CoreView;
import ru.surfstudio.android.core.ui.navigation.activity.navigator.ActivityNavigator;
import ru.surfstudio.android.rx.extension.ActionSafe;
import ru.surfstudio.android.rx.extension.ConsumerSafe;
import ru.surfstudio.android.rx.extension.scheduler.SchedulersProvider;

/**
 * базовый класс презентра для приложения
 * Подписки через все виды методов {@link #subscribe}, {@link #subscribeWithoutFreezing},
 * {@link #subscribeIoHandleError} обрабатываются в главном потоке
 * При подписке с помощью методов {@link #subscribeIoHandleError} observable источника переводится в
 * background поток.
 * Кроме того {@link #subscribeIoHandleError} содержит стандартную обработку ошибок
 * <p>
 * Кроме того содержит метод {@link #finish()} для закрытия текущего экрана, в дефолтной
 * имплементации закрывает активити
 * <p>
 * Имеет методы subscribe c постфиксом AutoReload, в которые следует передать лямбду, которая будет
 * вызвана при появлении интернета, если во время запроса интернета не было. Только последний запрос
 * подписанный с помощью этих методов будет с таким свойством
 * см {@link CorePresenter}
 *
 * @param <V>
 */
public abstract class BasePresenter<V extends CoreView> extends CorePresenter<V> {

    private final ActivityNavigator activityNavigator;
    private final SchedulersProvider schedulersProvider;
    private final ConnectionProvider connectionProvider;
    private ErrorHandler errorHandler;
    private Disposable autoReloadDisposable;

    public BasePresenter(BasePresenterDependency basePresenterDependency) {
        super(basePresenterDependency.getEventDelegateManager(), basePresenterDependency.getScreenState());
        this.schedulersProvider = basePresenterDependency.getSchedulersProvider();
        this.activityNavigator = basePresenterDependency.getActivityNavigator();
        this.connectionProvider = basePresenterDependency.getConnectionProvider();
        this.errorHandler = basePresenterDependency.getErrorHandler();
    }

    //region subscribe

    @Override
    protected <T> Disposable subscribe(final Observable<T> observable,
                                       final ObservableOperatorFreeze<T> operator,
                                       final LambdaObserver<T> observer) {
        return super.subscribe(observable.observeOn(schedulersProvider.main(), true), operator, observer);
    }

    @Override
    protected <T> Disposable subscribe(Single<T> single,
                                       SingleOperatorFreeze<T> operator,
                                       DisposableSingleObserver<T> observer) {
        return super.subscribe(single.observeOn(schedulersProvider.main()), operator, observer);
    }

    @Override
    protected Disposable subscribe(Completable completable,
                                   CompletableOperatorFreeze operator,
                                   DisposableCompletableObserver observer) {
        return super.subscribe(completable.observeOn(schedulersProvider.main()), operator, observer);
    }

    @Override
    protected <T> Disposable subscribe(Maybe<T> maybe,
                                       MaybeOperatorFreeze<T> operator,
                                       DisposableMaybeObserver<T> observer) {
        return super.subscribe(maybe.observeOn(schedulersProvider.main()), operator, observer);
    }
    //endregion

    //region subscribeWithoutFreezing
    @Override
    protected <T> Disposable subscribeWithoutFreezing(final Observable<T> observable,
                                                      final LambdaObserver<T> observer) {
        return super.subscribe(observable.observeOn(schedulersProvider.main(), true), observer);
    }

    @Override
    protected <T> Disposable subscribeWithoutFreezing(Single<T> single,
                                                      DisposableSingleObserver<T> subscriber) {
        return super.subscribeWithoutFreezing(single.observeOn(schedulersProvider.main()), subscriber);
    }

    @Override
    protected Disposable subscribeWithoutFreezing(Completable completable,
                                                  DisposableCompletableObserver subscriber) {
        return super.subscribeWithoutFreezing(completable.observeOn(schedulersProvider.main()), subscriber);
    }

    @Override
    protected <T> Disposable subscribeWithoutFreezing(Maybe<T> maybe, DisposableMaybeObserver<T> subscriber) {
        return super.subscribeWithoutFreezing(maybe.observeOn(schedulersProvider.main()), subscriber);
    }

    //endregion

    /**
     * закрывает текущую активити
     * если необходима другая логика закрытия экрана, следует переопределить этот метод
     */
    public void finish() {
        activityNavigator.finishCurrent();
    }

    /**
     * Устанавливает {@link ErrorHandler} вместо дефолтного
     */
    @NonNull
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Стандартная обработка ошибки в презентере
     *
     * Переопределяем в случае если нужно специфичная обработка
     * @param e ошибка
     */
    protected void handleError(Throwable e) {
        errorHandler.handleError(e);
    }

    //region subscribeIoHandleError

    /**
     * Работает также как {@link #subscribe}, кроме того автоматически обрабатывает ошибки,
     * см {@link ErrorHandler} и переводит выполенения потока в фон
     */
    protected <T> Disposable subscribeIoHandleError(Observable<T> observable,
                                                    final ConsumerSafe<T> onNext) {
        return subscribeIoHandleError(observable, onNext, null);
    }

    protected <T> Disposable subscribeIoHandleError(Single<T> single,
                                                    final ConsumerSafe<T> onSuccess) {
        return subscribeIoHandleError(single, onSuccess, null);
    }

    protected Disposable subscribeIoHandleError(Completable completable,
                                                final ActionSafe onComplete) {
        return subscribeIoHandleError(completable, onComplete, null);
    }

    protected <T> Disposable subscribeIoHandleError(Maybe<T> maybe,
                                                    final ConsumerSafe<T> onSuccess,
                                                    final ActionSafe onComplete) {
        return subscribeIoHandleError(maybe, onSuccess, onComplete, null);
    }

    protected <T> Disposable subscribeIoHandleError(Observable<T> observable,
                                                    final ConsumerSafe<T> onNext,
                                                    final ConsumerSafe<Throwable> onError) {
        observable = observable.subscribeOn(schedulersProvider.worker());
        return subscribe(observable, onNext, e -> handleError(e, onError));
    }

    protected <T> Disposable subscribeIoHandleError(Observable<T> observable,
                                                    final ConsumerSafe<T> onNext,
                                                    final ActionSafe onComplete,
                                                    final ConsumerSafe<Throwable> onError) {
        observable = observable.subscribeOn(schedulersProvider.worker());
        return subscribe(observable, onNext, onComplete, e -> handleError(e, onError));
    }

    protected <T> Disposable subscribeIoHandleError(Single<T> single,
                                                    final ConsumerSafe<T> onSuccess,
                                                    final ConsumerSafe<Throwable> onError) {
        single = single.subscribeOn(schedulersProvider.worker());
        return subscribe(single, onSuccess, e -> handleError(e, onError));
    }

    protected Disposable subscribeIoHandleError(Completable completable,
                                                final ActionSafe onComplete,
                                                final ConsumerSafe<Throwable> onError) {
        completable = completable.subscribeOn(schedulersProvider.worker());
        return subscribe(completable, onComplete, e -> handleError(e, onError));
    }

    protected <T> Disposable subscribeIoHandleError(Maybe<T> maybe,
                                                    final ConsumerSafe<T> onSuccess,
                                                    final ActionSafe onComplete,
                                                    final ConsumerSafe<Throwable> onError) {
        maybe = maybe.subscribeOn(schedulersProvider.worker());
        return subscribe(maybe, onSuccess, onComplete, e -> handleError(e, onError));
    }


    //endregion

    //region subscribeIo
    protected <T> Disposable subscribeIo(Observable<T> observable,
                                         final ConsumerSafe<T> onNext,
                                         final ConsumerSafe<Throwable> onError) {
        observable = observable.subscribeOn(schedulersProvider.worker());
        return subscribe(observable, onNext, onError);
    }

    protected <T> Disposable subscribeIo(Single<T> single,
                                         final ConsumerSafe<T> onSuccess,
                                         final ConsumerSafe<Throwable> onError) {
        single = single.subscribeOn(schedulersProvider.worker());
        return subscribe(single, onSuccess, onError);
    }

    protected Disposable subscribeIo(Completable completable,
                                     final ActionSafe onComplete,
                                     final ConsumerSafe<Throwable> onError) {
        completable = completable.subscribeOn(schedulersProvider.worker());
        return subscribe(completable, onComplete, onError);
    }

    protected <T> Disposable subscribeIo(Observable<T> observable,
                                         final ConsumerSafe<T> onNext,
                                         final ActionSafe onComplete,
                                         final ConsumerSafe<Throwable> onError) {
        observable = observable.subscribeOn(schedulersProvider.worker());
        return subscribe(observable, onNext, onComplete, onError);
    }

    protected <T> Disposable subscribeIo(Maybe<T> maybe,
                                         final ConsumerSafe<T> onSuccess,
                                         final ActionSafe onComplete,
                                         final ConsumerSafe<Throwable> onError) {
        maybe = maybe.subscribeOn(schedulersProvider.worker());
        return subscribe(maybe, onSuccess, onComplete, onError);
    }
    //endregion

    //region subscribeIoAutoReload

    /**
     * {@see subscribeIo}
     * автоматически вызовет autoReloadAction при появлении интернета если на момент выполнения
     * observable не было подключения к интернету
     */
    protected <T> Disposable subscribeIoAutoReload(Observable<T> observable,
                                                   final ActionSafe autoReloadAction,
                                                   final ConsumerSafe<T> onNext,
                                                   final ConsumerSafe<Throwable> onError) {
        return subscribeIo(initializeAutoReload(observable, autoReloadAction), onNext, onError);
    }

    protected <T> Disposable subscribeIoAutoReload(Observable<T> observable,
                                                   final ActionSafe autoReloadAction,
                                                   final ConsumerSafe<T> onNext,
                                                   final ActionSafe onComplete,
                                                   final ConsumerSafe<Throwable> onError) {
        return subscribeIo(initializeAutoReload(observable, autoReloadAction), onNext, onComplete, onError);
    }

    protected <T> Disposable subscribeIoAutoReload(Single<T> single,
                                                   final ActionSafe autoReloadAction,
                                                   final ConsumerSafe<T> onSuccess,
                                                   final ConsumerSafe<Throwable> onError) {
        return subscribeIo(initializeAutoReload(single, autoReloadAction), onSuccess, onError);
    }

    protected Disposable subscribeIoAutoReload(Completable completable,
                                               final ActionSafe autoReloadAction,
                                               final ActionSafe onComplete,
                                               final ConsumerSafe<Throwable> onError) {
        return subscribeIo(initializeAutoReload(completable, autoReloadAction), onComplete, onError);
    }

    protected <T> Disposable subscribeIoAutoReload(Maybe<T> maybe,
                                                   final ActionSafe autoReloadAction,
                                                   final ConsumerSafe<T> onSuccess,
                                                   final ActionSafe onComplete,
                                                   final ConsumerSafe<Throwable> onError) {
        return subscribeIo(initializeAutoReload(maybe, autoReloadAction), onSuccess, onComplete, onError);
    }
    //endregion

    //region subscribeIoHandleErrorAutoReload

    /**
     * {@see subscribeIoAutoReload} кроме того автоматически обрабатывает ошибки
     */
    protected <T> Disposable subscribeIoHandleErrorAutoReload(Observable<T> observable,
                                                              final ActionSafe autoReloadAction,
                                                              final ConsumerSafe<T> onNext,
                                                              @Nullable final ConsumerSafe<Throwable> onError) {
        return subscribeIoHandleError(initializeAutoReload(observable, autoReloadAction), onNext, onError);
    }

    protected <T> Disposable subscribeIoHandleErrorAutoReload(Observable<T> observable,
                                                              final ActionSafe autoReloadAction,
                                                              final ConsumerSafe<T> onNext,
                                                              final ActionSafe onComplete,
                                                              @Nullable final ConsumerSafe<Throwable> onError) {
        return subscribeIoHandleError(initializeAutoReload(observable, autoReloadAction), onNext, onComplete, onError);
    }

    protected <T> Disposable subscribeIoHandleErrorAutoReload(Single<T> single,
                                                              final ActionSafe autoReloadAction,
                                                              final ConsumerSafe<T> onSuccess,
                                                              @Nullable final ConsumerSafe<Throwable> onError) {
        return subscribeIoHandleError(initializeAutoReload(single, autoReloadAction), onSuccess, onError);
    }

    protected Disposable subscribeIoHandleErrorAutoReload(Completable completable,
                                                          final ActionSafe autoReloadAction,
                                                          final ActionSafe onComplete,
                                                          @Nullable final ConsumerSafe<Throwable> onError) {
        return subscribeIoHandleError(initializeAutoReload(completable, autoReloadAction), onComplete, onError);
    }

    protected <T> Disposable subscribeIoHandleErrorAutoReload(Maybe<T> maybe,
                                                              final ActionSafe autoReloadAction,
                                                              final ConsumerSafe<T> onSuccess,
                                                              final ActionSafe onComplete,
                                                              @Nullable final ConsumerSafe<Throwable> onError) {
        return subscribeIoHandleError(initializeAutoReload(maybe, autoReloadAction), onSuccess, onComplete, onError);
    }

    //endregion

    private void handleError(Throwable e, @Nullable ConsumerSafe<Throwable> onError) {
        handleError(e);
        if (onError != null) {
            onError.accept(e);
        }
    }

    @NonNull
    private <T> Observable<T> initializeAutoReload(Observable<T> observable, ActionSafe reloadAction) {
        return observable.doOnError(reloadErrorAction(reloadAction));
    }

    @NonNull
    private <T> Single<T> initializeAutoReload(Single<T> single, ActionSafe reloadAction) {
        return single.doOnError(reloadErrorAction(reloadAction));
    }

    @NonNull
    private Completable initializeAutoReload(Completable completable, ActionSafe reloadAction) {
        return completable.doOnError(reloadErrorAction(reloadAction));
    }

    @NonNull
    private <T> Maybe<T> initializeAutoReload(Maybe<T> maybe, ActionSafe reloadAction) {
        return maybe.doOnError(reloadErrorAction(reloadAction));
    }

    private ConsumerSafe<Throwable> reloadErrorAction(ActionSafe reloadAction) {
        return e -> {
            cancelAutoReload();
            if (connectionProvider.isDisconnected()) {
                autoReloadDisposable = subscribe(connectionProvider.observeConnectionChanges()
                                .filter(connected -> connected)
                                .firstElement()
                                .toObservable(),
                        connected -> reloadAction.run());
            }
        };
    }

    private void cancelAutoReload() {
        if (isDisposableActive(autoReloadDisposable)) {
            autoReloadDisposable.dispose();
        }
    }
}
