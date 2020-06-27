package ru.surfstudio.android.navigation.sample_standard.screen.guide

import android.util.Log
import ru.surfstudio.android.core.mvp.binding.rx.ui.BaseRxPresenter
import ru.surfstudio.android.core.mvp.presenter.BasePresenterDependency
import ru.surfstudio.android.dagger.scope.PerScreen
import ru.surfstudio.android.navigation.command.fragment.Replace
import ru.surfstudio.android.navigation.executor.NavigationCommandExecutor
import ru.surfstudio.android.navigation.sample_standard.screen.bottom_navigation.BottomNavigationRoute
import javax.inject.Inject

@PerScreen
class GuidePresenter @Inject constructor(
        basePresenterDependency: BasePresenterDependency,
        private val bm: GuideBindModel,
        private val commandExecutor: NavigationCommandExecutor
) : BaseRxPresenter(basePresenterDependency) {

    override fun onFirstLoad() {
        bm.bottomNavClicked.bindTo {
            Log.d("111111", "Bottom clicked")
            commandExecutor.execute(Replace(BottomNavigationRoute()))
        }
    }
}