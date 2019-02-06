/*
  Copyright (c) 2018-present, SurfStudio LLC, Fedor Atyakshin, Artem Zaytsev.

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
package ru.surfstudio.android.notification.ui.notification.strategies

import android.app.Activity
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import android.widget.RemoteViews
import androidx.annotation.StringRes
import ru.surfstudio.android.core.ui.navigation.activity.route.ActivityRoute
import ru.surfstudio.android.notification.R
import ru.surfstudio.android.notification.interactor.push.BaseNotificationTypeData
import ru.surfstudio.android.notification.interactor.push.PushInteractor
import ru.surfstudio.android.notification.ui.notification.NotificationCreateHelper

/**
 * Стратегия обработки пуша
 *
 * Позволяет задать вид нотификации(при активном приложении в случае NotificationMessage,
 * при активном и неактивнм в случае data message)
 *
 * Для Android O необходимо указать id канала
 */
abstract class PushHandleStrategy<out T : BaseNotificationTypeData<*>> {
    /**
     * тип данных пуша [BaseNotificationTypeData]
     */
    abstract val typeData: T

    /**
     * Id канала пуш уведомлений(строковый ресурс)
     */
    abstract val channelId: Int

    /**
     * Имя канала
     */
    @StringRes
    open val channelName: Int = R.string.default_channel_name

    /**
     * Иконка
     */
    abstract val icon: Int

    /**
     * Цвет иконки пуш уведомлений(ресурс)
     */
    abstract val color: Int

    /**
     * Флаг автозакрытия
     */
    abstract val autoCancelable: Boolean

    /**
     * Кастомный вид пуш-уведомления
     */
    abstract val contentView: RemoteViews?

    /**
     * Канал пуш уведомлений(Android O [https://developer.android.com/training/notify-user/channels.html?hl=ru])
     */
    var channel: NotificationChannel? = null

    /**
     * Кастомный builder для пушей(если нужно какое-то особое поведение)
     */
    var notificationBuilder: NotificationCompat.Builder? = null

    /**
     * Действия при нажатии на пуш
     */
    lateinit var pendingIntent: PendingIntent

    /**
     * Требуемое действие нотификации
     *
     * @param context      текущий контекст
     * @param pushInteractor пуш интерактор
     * @param title          заголвок пуша
     * @param body           сообщение пуша
     * @param data           данные пуша
     */
    open fun handle(
            context: Context,
            pushInteractor: PushInteractor,
            title: String,
            body: String
    ) {

        pendingIntent = preparePendingIntent(context, title)
        notificationBuilder = makeNotificationBuilder(context, title, body)
        channel = makeNotificationChannel(context, title)

        pushInteractor.onNewNotification(typeData)

        when (context) {
            is Activity -> if (!handlePushInActivity(context)) {
                showNotification(context, title, body)
            }

            else -> showNotification(context, title, body)
        }
    }

    private fun showNotification(context: Context, title: String, body: String) {
        NotificationCreateHelper.showNotification(
                context,
                this,
                title,
                body
        )
    }

    /**
     * Определяем в каких случайх пуш не надо отображать,
     * а необходимо выполнить те или иные действия по подписке
     */
    abstract fun handlePushInActivity(activity: Activity): Boolean

    /**
     * Интент в соответствии с необходимыми действиями
     */
    abstract fun preparePendingIntent(context: Context, title: String): PendingIntent

    /**
     * Интент при нажатии на пуш, если приложение в бэкграунде
     */
    abstract fun coldStartRoute(): ActivityRoute

    /**
     * Метод для создания билдера ноификаций
     */
    abstract fun makeNotificationBuilder(context: Context, title: String, body: String): NotificationCompat.Builder?

    /**
     * Метод для создания канала нотификаций
     */
    abstract fun makeNotificationChannel(context: Context, title: String): NotificationChannel?
}