package ru.surfstudio.android.firebase.sample.ui.common.notification.strategies.simple

import android.app.PendingIntent
import android.content.Context
import ru.surfstudio.android.core.ui.navigation.activity.route.ActivityRoute
import ru.surfstudio.android.firebase.sample.R
import ru.surfstudio.android.firebase.sample.ui.common.notification.strategies.type.NotificationTypeData
import ru.surfstudio.android.firebase.sample.ui.screen.push.data.DataPushActivityRoute

/**
 * Стратегия для пушей с данными
 */
class DataPushStrategy : BaseSimplePushStrategy<NotificationTypeData>() {
    override val typeData by lazy { NotificationTypeData() }

    override val channelName: Int = R.string.data_push_channel_name

    override fun coldStartRoute(): ActivityRoute = DataPushActivityRoute(typeData.data)

    override fun preparePendingIntent(context: Context, title: String): PendingIntent {
        return PendingIntent.getActivity(
                context,
                title.hashCode(),
                coldStartRoute().prepareIntent(context),
                PendingIntent.FLAG_ONE_SHOT)
    }
}