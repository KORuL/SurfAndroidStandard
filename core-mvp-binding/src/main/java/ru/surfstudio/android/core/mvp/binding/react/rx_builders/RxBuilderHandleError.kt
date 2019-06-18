package ru.surfstudio.android.core.mvp.binding.react.rx_builders

import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import ru.surfstudio.android.core.mvp.error.ErrorHandler
import ru.surfstudio.android.rx.extension.scheduler.SchedulersProvider

interface RxBuilderHandleError {

    val schedulersProvider: SchedulersProvider
    val errorHandler: ErrorHandler

    /**
     * Build-функция, обрабатывающая ошибки [Single] с помощью [ErrorHandler] в главном потоке.
     */
    fun <T> Single<T>.handleError(): Single<T> = this
            .doOnError(::handleErrorOnMainThread)

    /**
     * Build-функция, обрабатывающая ошибки [Observable] с помощью [ErrorHandler] в главном потоке.
     */
    fun <T> Observable<T>.handleError(): Observable<T> = this
            .doOnError(::handleErrorOnMainThread)

    /**
     * Build-функция, обрабатывающая ошибки [Maybe] с помощью [ErrorHandler] в главном потоке.
     */
    fun <T> Maybe<T>.handleError(): Maybe<T> = this
            .doOnError(::handleErrorOnMainThread)

    /**
     * Build-функция, обрабатывающая ошибки [Completable] с помощью [ErrorHandler] в главном потоке.
     */
    fun Completable.handleError(): Completable = this
            .doOnError(::handleErrorOnMainThread)

    fun handleErrorOnMainThread(throwable: Throwable) {
        schedulersProvider.main().scheduleDirect { errorHandler.handleError(throwable) }
    }

}