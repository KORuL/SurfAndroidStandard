package ru.surfstudio.android.core.ui.sample.ui.screen.main

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.annotation.IdRes
import kotlinx.android.synthetic.main.activity_main.*
import ru.surfstudio.android.core.mvp.activity.BaseRenderableActivityView
import ru.surfstudio.android.core.mvp.presenter.CorePresenter
import ru.surfstudio.android.core.ui.FragmentContainer
import ru.surfstudio.android.core.ui.sample.R
import ru.surfstudio.android.core.ui.sample.ui.core.CustomOnRestoreStateDelegate
import ru.surfstudio.android.core.ui.sample.ui.core.CustomOnSaveStateDelegate
import ru.surfstudio.android.message.MessageController
import ru.surfstudio.android.sample.dagger.ui.base.configurator.DefaultActivityScreenConfigurator
import javax.inject.Inject

/**
 * Вью главного экрана
 */
class MainActivityView : BaseRenderableActivityView<MainScreenModel>(), FragmentContainer {

    @Inject
    internal lateinit var presenter: MainPresenter

    @Inject
    internal lateinit var messageController: MessageController

    @IdRes
    override fun getContentView(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?,
                          persistentState: PersistableBundle?,
                          viewRecreated: Boolean) {
        super.onCreate(savedInstanceState, persistentState, viewRecreated)
        // Регистрация делегата только при первом старте экрана
        if (!viewRecreated) {
            val delegateManager = persistentScope.screenEventDelegateManager
            val saveStateDelegate = CustomOnSaveStateDelegate(delegateManager)
            val restoreStateDelegate = CustomOnRestoreStateDelegate(delegateManager)
            messageController.show(R.string.snackbar_message)
        }
        initListeners()
    }

    override fun getContentContainerViewId(): Int = R.id.container

    override fun getScreenName(): String  = "MainActivity"

    override fun renderInternal(sm: MainScreenModel) {}

    override fun getPresenters(): Array<CorePresenter<*>> = arrayOf(presenter)

    override fun createConfigurator(): DefaultActivityScreenConfigurator = MainScreenConfigurator(intent)

    private fun initListeners() {
        button_to_fragment_2.setOnClickListener { presenter.openFragment2() }
    }

    fun showMessage(data: String) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
    }
}
