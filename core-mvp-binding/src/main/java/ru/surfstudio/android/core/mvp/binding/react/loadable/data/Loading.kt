package ru.surfstudio.android.core.mvp.binding.react.loadable.data

/**
 * Обертка для отображения состояния загрузки на Ui.
 *
 * Можно реализовать у себя в проекте все необходимые состояния, унаследовавшись от этого класса.
 */
interface Loading {
    val isLoading: Boolean
}

/**
 * Состояние загрузки, когда на экране нет контента
 */
class MainLoading(override val isLoading: Boolean) : Loading

/**
 * Состояние загрузки, когда на экране есть какой-то контент
 */
class TransparentLoading(override val isLoading: Boolean) : Loading

/**
 * Состояние загрузки при вызове через SwipeRefresh
 */
class SwipeRefreshLoading(override val isLoading: Boolean) : Loading