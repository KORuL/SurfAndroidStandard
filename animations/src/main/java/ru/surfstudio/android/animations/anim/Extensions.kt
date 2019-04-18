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
package ru.surfstudio.android.animations.anim

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Extension для анимации View
 */

/**
 * Появление вью с изменением прозрачности
 *
 * @see [AnimationUtil.fadeIn]
 */
fun View.fadeIn(
        duration: Long = AnimationUtil.ANIM_ENTERING,
        defaultAlpha: Float = alpha,
        endAction: (() -> Unit)? = null
) =
        AnimationUtil.fadeIn(this, duration, defaultAlpha, endAction)

/**
 * Сокрытие вью с изменением прозрачности
 *
 * @see [AnimationUtil.fadeOut]
 */
fun View.fadeOut(
        duration: Long = AnimationUtil.ANIM_LEAVING,
        endVisibility: Int = View.GONE,
        defaultAlpha: Float = alpha,
        endAction: (() -> Unit)? = null
) =
        AnimationUtil.fadeOut(this, duration, endVisibility, defaultAlpha, endAction)

/**
 * Появление вью с эффектом "слайда" в зависимости от gravity
 */
fun View.slideIn(gravity: Int,
                 duration: Long = AnimationUtil.ANIM_ENTERING,
                 interpolator: TimeInterpolator = LinearInterpolator(),
                 startAction: ((View) -> Unit)? = null,
                 endAction: ((View) -> Unit)? = null) =
        AnimationUtil.slideIn(this, gravity, duration, interpolator, startAction, endAction)

/**
 * Исчезновение вью с эффектом "слайда" в зависимости от gravity
 */
fun View.slideOut(gravity: Int,
                  duration: Long = AnimationUtil.ANIM_LEAVING,
                  interpolator: TimeInterpolator = LinearInterpolator(),
                  startAction: ((View) -> Unit)? = null,
                  endAction: ((View) -> Unit)? = null) =
        AnimationUtil.slideOut(this, gravity, duration, interpolator, startAction, endAction)

/**
 * Анимация типа "пульс"
 */
fun View.pulseAnimation(scale: Float = 1.15f,
                        duration: Long = AnimationUtil.ANIM_PULSATION,
                        repeatCount: Int = ObjectAnimator.INFINITE,
                        repeatMode: Int = ObjectAnimator.REVERSE,
                        interpolator: TimeInterpolator = FastOutLinearInInterpolator()) =
        AnimationUtil.pulseAnimation(this, scale, duration, repeatCount, repeatMode, interpolator)

/**
 * Изменение ширины и высоты вью
 */
fun View.toSize(newWidth: Int, newHeight: Int,
                duration: Long = AnimationUtil.ANIM_TRANSITION) =
        AnimationUtil.newSize(this, newWidth, newHeight, duration)