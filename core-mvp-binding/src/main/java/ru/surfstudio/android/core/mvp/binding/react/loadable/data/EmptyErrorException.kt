package ru.surfstudio.android.core.mvp.binding.react.loadable.data

import java.lang.Exception

/**
 * Пустая ошибка.
 * Используется, если запрос вернул положительный результат, и предыдущий errorState необходимо очистить.
 */
class EmptyErrorException : Exception()