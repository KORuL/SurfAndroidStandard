package ru.surfstudio.standard.ui.view.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.surfstudio.android.easyadapter.ItemList
import ru.surfstudio.android.template.base_feature.R
import ru.surfstudio.android.utilktx.util.ViewUtil
import ru.surfstudio.standard.ui.view.keyboard.CustomKeyboardUtils.createKeyBoard
import ru.surfstudio.standard.ui.view.keyboard.controller.EmptyKeyController
import ru.surfstudio.standard.ui.view.keyboard.controller.IconController
import ru.surfstudio.standard.ui.view.keyboard.controller.KeyController

/**
 * View клавиатуры
 */
class KeyboardView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttrs: Int = 0
) : RecyclerView(context, attrs, defStyleAttrs) {

    var buttonLeft: Key? = null
        set(value) {
            field = value
            create()
        }
    var buttonRight: Key? = null
        set(value) {
            field = value
            create()
        }

    var onKeyClick: (code: String) -> Unit = {}
    var onDeleteClick = {}

    private val easyAdapter = EasyAdapter().apply { isFirstInvisibleItemEnabled = true }

    private val keyController = KeyController { onKeyClick(it) }
    private val deleteController = IconController { onDeleteClick() }
    private val emptyController = EmptyKeyController()

    init {
        initAttrs(attrs)
        layoutManager = GridLayoutManager(context, KEYBOARD_COLUMNS_COUNT)
        overScrollMode = View.OVER_SCROLL_NEVER
        adapter = easyAdapter

        create()
    }

    private fun create() {
        easyAdapter.setItems(
                ItemList.create().apply {
                    createKeyBoard(buttonLeft, buttonRight).forEach {
                        when (it) {
                            is TextKey -> add(it, keyController)
                            is IconKey -> add(it, deleteController)
                            is EmptyKey -> add(it, emptyController)
                        }
                    }
                }
        )
    }

    private fun initAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.KeyboardView).apply {
            keyController.isShowLetters = getBoolean(R.styleable.KeyboardView_isShowLetters, true)

            keyController.titleTextColor = getColor(R.styleable.KeyboardView_titleTextColor, KeyView.DEFAULT_TITLE_COLOR)
            keyController.subtitleTextColor = getColor(R.styleable.KeyboardView_subtitleTextColor, KeyView.DEFAULT_SUBTITLE_COLOR)

            keyController.titleTextSize = getDimension(
                    R.styleable.KeyboardView_titleTextSize,
                    ViewUtil.convertDpToPx(context, KeyView.DEFAULT_TITLE_SIZE).toFloat()
            )
            keyController.subtitleTextSize = getDimension(
                    R.styleable.KeyboardView_subtitleTextSize,
                    ViewUtil.convertDpToPx(context, KeyView.DEFAULT_SUBTITLE_SIZE).toFloat()
            )

            keyController.titleFont = getResourceId(R.styleable.KeyboardView_titleFont, KeyView.UNDEFINE_ATTR)
            keyController.subtitleFont = getResourceId(R.styleable.KeyboardView_subtitleFont, KeyView.UNDEFINE_ATTR)

            val leftButtonText = getString(R.styleable.KeyboardView_leftButtonText)
            leftButtonText?.let {
                buttonLeft = TextKey(leftButtonText)
            }
            val rightButtonText = getString(R.styleable.KeyboardView_rightButtonText)
            rightButtonText?.let {
                buttonRight = TextKey(rightButtonText)
            }

            val leftButtonIcon = getResourceId(R.styleable.KeyboardView_leftButtonIcon, KeyView.UNDEFINE_ATTR)
            if (leftButtonIcon != KeyView.UNDEFINE_ATTR) {
                buttonLeft = IconKey(leftButtonIcon)
            }
            val rightButtonIcon = getResourceId(R.styleable.KeyboardView_rightButtonIcon, KeyView.UNDEFINE_ATTR)
            if (rightButtonIcon != KeyView.UNDEFINE_ATTR) {
                buttonRight = IconKey(rightButtonIcon)
            }
        }.recycle()
    }

    companion object {

        const val KEYBOARD_COLUMNS_COUNT = 3
    }
}
