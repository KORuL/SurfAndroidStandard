package ru.surfstudio.standard.i_network.service;

import io.reactivex.Observable;

import ru.surfstudio.standard.i_network.error.exception.BaseWrappedHttpException;
import ru.surfstudio.standard.i_network.error.exception.HttpProtocolException;
import ru.surfstudio.standard.i_network.error.handler.BaseErrorHandler;
import ru.surfstudio.standard.i_network.network.error.HttpCodes;

/**
 * Бзовый класс для работы с api
 */
public class BaseNetworkService {

    protected <T> Observable<T> call(Observable<T> observable, BaseErrorHandler errorHandler) {
        return observable.onErrorResumeNext((Throwable throwable) -> handleError(throwable, errorHandler));
    }

    private <T> Observable<T> handleError(Throwable throwable, BaseErrorHandler errorHandler) {
        if (throwable instanceof BaseWrappedHttpException) {
            final BaseWrappedHttpException wrappedHttpException = (BaseWrappedHttpException) throwable;
            HttpProtocolException httpException = wrappedHttpException.getHttpCause();
            int httpCode = httpException.getHttpCode();

            if (httpCode == HttpCodes.CODE_400) {
                errorHandler.handle(wrappedHttpException);
            }
        }
        return Observable.error(throwable);
    }
}