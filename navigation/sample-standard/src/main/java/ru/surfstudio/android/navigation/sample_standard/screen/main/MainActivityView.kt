package ru.surfstudio.android.navigation.sample_standard.screen.main

import ru.surfstudio.android.core.mvp.binding.rx.ui.BaseRxActivityView
import ru.surfstudio.android.navigation.sample_standard.R

class MainActivityView : BaseRxActivityView() {

    override fun createConfigurator() = MainConfigurator(intent)

    override fun getContentView(): Int = R.layout.activity_main

    override fun getScreenName(): String = "Main"
}