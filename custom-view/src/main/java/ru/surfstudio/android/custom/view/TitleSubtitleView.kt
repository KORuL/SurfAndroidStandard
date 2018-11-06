/*
  Copyright (c) 2018-present, SurfStudio LLC.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package ru.surfstudio.android.custom.view

import android.content.Context
import android.content.res.TypedArray
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView

private const val DEFAULT_MAX_LINES: Int = 1

/**
 * Виджет для отображения текста с подписью
 *
 * Изменять параметры форматирования текста можно посредством атрибутов в xml.
 * Возможно изменение заголовка и подписи по отдельности.
 */
class TitleSubtitleView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attributeSet, defStyleAttr) {

    private var titleView: TextView = TextView(context)
    private var subTitleView: TextView = TextView(context)

    private var defaultTitle: String = "Title"
        set(value) {
            field = value
            titleView.text = field
        }

    private var defaultSubTitle: String = "SubTitle"
        set(value) {
            field = value
            subTitleView.text = field
        }

    var titleText: String = defaultTitle
        set(value) {
            field = value
            updateView()
        }

    var subTitleText: String = defaultSubTitle
        set(value) {
            field = value
            updateView()
        }

    var onTitleClickListenerCallback: ((String) -> Unit)? = null
        set(value) {
            if (value != null) {
                titleView.setOnClickListener { value(titleText) }
            } else {
                titleView.setOnClickListener(null)
                titleView.isClickable = false
            }
        }

    var onSubTitleClickListenerCallback: ((String) -> Unit)? = null
        set(value) {
            if (value != null) {
                subTitleView.setOnClickListener { value(subTitleText) }
            } else {
                subTitleView.setOnClickListener(null)
                subTitleView.isClickable = false
            }
        }

    init {
        orientation = LinearLayout.VERTICAL

        addViews()
        applyAttrs(attributeSet)
    }

    /**
     * Возвращает заголовок к дефолтному значению
     */
    fun resetTitleText() {
        titleText = defaultTitle
    }

    /**
     * Возвращает подзаголовок к дефолтному значению
     */
    fun resetSubTitleText() {
        subTitleText = defaultSubTitle
    }

    private fun addViews() {
        addView(titleView)
        addView(subTitleView)
    }

    private fun updateView() {
        titleView.text = titleText
        subTitleView.text = subTitleText
    }

    private fun applyAttrs(attrs: AttributeSet?) {
        val ta: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TitleSubtitleView, 0, 0)

        //заголовок
        setupTitle(ta)
        //подзаголовок
        setupSubTitle(ta)

        ta.recycle()
    }

    private fun setupTitle(ta: TypedArray) {
        with(titleView) {
            defaultTitle = ta.getString(R.styleable.TitleSubtitleView_titleText) ?: defaultTitle
            titleText = defaultTitle

            setupTextAppearance(ta, R.styleable.TitleSubtitleView_titleTextAppearance)
            setupTextSize(ta, R.styleable.TitleSubtitleView_titleTextSize)
            setupTextColor(ta, R.styleable.TitleSubtitleView_titleTextColor)

            setLineSpacing(ta.getDimension(R.styleable.TitleSubtitleView_titleLineSpacingExtra, 0f), 1f)

            setPadding(
                    ta.getDimensionPixelOffset(R.styleable.TitleSubtitleView_titlePaddingStart, 0),
                    ta.getDimensionPixelOffset(R.styleable.TitleSubtitleView_titlePaddingTop, 0),
                    ta.getDimensionPixelOffset(R.styleable.TitleSubtitleView_titlePaddingEnd, 0),
                    ta.getDimensionPixelOffset(R.styleable.TitleSubtitleView_titlePaddingBottom, 0)
            )

            setLines(ta.getInt(R.styleable.TitleSubtitleView_titleLines, lineCount))
            maxLines = ta.getInt(R.styleable.TitleSubtitleView_titleMaxLines, DEFAULT_MAX_LINES)

            gravity = ta.getInt(R.styleable.TitleSubtitleView_titleGravity, gravity)

            setBackgroundColor(ta.getColor(
                    R.styleable.TitleSubtitleView_titleBackgroundColor,
                    ContextCompat.getColor(context, android.R.color.transparent)
            ))

            ellipsize = getEllipsizeFromResource(ta, R.styleable.TitleSubtitleView_titleEllipsize)
        }
    }

    private fun setupSubTitle(ta: TypedArray) {
        with(subTitleView) {
            defaultSubTitle = ta.getString(R.styleable.TitleSubtitleView_subTitleText) ?: defaultSubTitle
            subTitleText = defaultSubTitle

            setupTextAppearance(ta, R.styleable.TitleSubtitleView_subtitleTextAppearance)
            setupTextSize(ta, R.styleable.TitleSubtitleView_subTitleTextSize)
            setupTextColor(ta, R.styleable.TitleSubtitleView_subTitleTextColor)

            setLineSpacing(ta.getDimension(R.styleable.TitleSubtitleView_subTitleLineSpacingExtra, 0f), 1f)

            setPadding(
                    ta.getDimensionPixelOffset(R.styleable.TitleSubtitleView_subTitlePaddingStart, 0),
                    ta.getDimensionPixelOffset(R.styleable.TitleSubtitleView_subTitlePaddingTop, 0),
                    ta.getDimensionPixelOffset(R.styleable.TitleSubtitleView_subTitlePaddingEnd, 0),
                    ta.getDimensionPixelOffset(R.styleable.TitleSubtitleView_subTitlePaddingBottom, 0)
            )

            setLines(ta.getInt(R.styleable.TitleSubtitleView_subTitleLines, lineCount))
            maxLines = ta.getInt(R.styleable.TitleSubtitleView_subTitleMaxLines, DEFAULT_MAX_LINES)

            gravity = ta.getInt(R.styleable.TitleSubtitleView_subTitleGravity, gravity)

            setBackgroundColor(ta.getColor(
                    R.styleable.TitleSubtitleView_subTitleBackgroundColor,
                    ContextCompat.getColor(context, android.R.color.transparent)
            ))

            ellipsize = getEllipsizeFromResource(ta, R.styleable.TitleSubtitleView_subTitleEllipsize)
        }
    }

    private fun TextView.setupTextAppearance(ta: TypedArray, index: Int) {
        val ap = ta.getResourceId(index, -1)
        if (ap != -1) {
            TextViewCompat.setTextAppearance(this, ap)
        }
    }

    private fun TextView.setupTextSize(ta: TypedArray, index: Int) {
        val size = ta.getDimensionPixelSize(index, -1).toFloat()
        if (size > 0) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        }
    }

    private fun TextView.setupTextColor(ta: TypedArray, index: Int) {
        val textColor = ta.getColor(index, -1)
        if (textColor != -1) {
            setTextColor(textColor)
        }
    }

    private fun getEllipsizeFromResource(ta: TypedArray, index: Int): TextUtils.TruncateAt? =
            when (ta.getInt(index, 0)) {
                1 -> TextUtils.TruncateAt.START
                2 -> TextUtils.TruncateAt.MIDDLE
                3 -> TextUtils.TruncateAt.END
                4 -> TextUtils.TruncateAt.MARQUEE
                else -> TextUtils.TruncateAt.END
            }
}