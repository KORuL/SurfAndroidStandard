package ru.surfstudio.android.core.mvi.sample.ui.screen.input

import android.os.Bundle
import android.os.PersistableBundle
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.activity_input_form.*
import ru.surfstudio.android.core.mvi.event.hub.owner.SingleHubOwner
import ru.surfstudio.android.core.mvi.sample.R
import ru.surfstudio.android.core.mvi.sample.ui.base.hub.BaseEventHub
import ru.surfstudio.android.core.mvi.ui.BaseReactActivityView
import javax.inject.Inject
import ru.surfstudio.android.core.mvi.sample.ui.screen.input.InputFormEvent.*

/**
 * Экран с формой ввода текста и возвращением результата на предыдущий экран
 */
class InputFormActivityView : BaseReactActivityView(), SingleHubOwner<InputFormEvent> {

    @Inject
    override lateinit var hub: BaseEventHub<InputFormEvent>

    @Inject
    lateinit var sh: InputFormStateHolder

    override fun createConfigurator() = InputFormScreenConfigurator(intent)

    override fun getScreenName(): String = "InputFormActivityView"

    override fun getContentView(): Int = R.layout.activity_input_form

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?, viewRecreated: Boolean) {
        input_et.textChanges().emit { InputChanged(it.toString()) }
        submit_btn.clicks().emit(SubmitClicked)
    }
}