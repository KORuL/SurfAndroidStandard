package ru.surfstudio.android.firebase.sample.ui.screen.main

import ru.surfstudio.android.firebase.sample.interactor.common.analytics.AnalyticsService
import ru.surfstudio.android.core.mvp.presenter.BasePresenter
import ru.surfstudio.android.core.mvp.presenter.BasePresenterDependency
import ru.surfstudio.android.dagger.scope.PerScreen
import javax.inject.Inject

/**
 * Презентер главного экрана
 */
@PerScreen
internal class MainPresenter @Inject constructor(basePresenterDependency: BasePresenterDependency,
                                                 private val analyticsService: AnalyticsService
) : BasePresenter<MainActivityView>(basePresenterDependency) {

    private val sm: MainScreenModel = MainScreenModel()

    override fun onLoad(viewRecreated: Boolean) {
        super.onLoad(viewRecreated)
        view.render(sm)
    }

    fun sendEvent(stringValue: String) {
        analyticsService.trackEvent(stringValue)
    }
}