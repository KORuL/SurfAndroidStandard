package ru.surfstudio.android.firebase.sample.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import ru.surfstudio.android.activity.holder.ActiveActivityHolder
import ru.surfstudio.android.firebase.sample.BuildConfig
import ru.surfstudio.android.logger.Logger
import ru.surfstudio.android.notification.ui.PushClickProvider
import ru.surfstudio.android.notification.ui.PushEventListener
import ru.surfstudio.android.sample.dagger.app.DefaultActivityLifecycleCallbacks

/**
 * Класс приложения
 */
class CustomApp : MultiDexApplication() {

    val activeActivityHolder = ActiveActivityHolder()

    override fun onCreate() {
        super.onCreate()
        initFabric()
        initInjector()
        initNotificationCenter()
        registerActiveActivityListener()
        initPushEventListener()
    }

    private fun initFabric() {
        Fabric.with(this, *getFabricKits())
    }

    private fun getFabricKits() = arrayOf(Crashlytics.Builder()
            .core(CrashlyticsCore.Builder()
                    .disabled(BuildConfig.DEBUG)
                    .build())
            .build())

    private fun initInjector() {
        AppConfigurator.initInjector(this)
    }

    private fun initNotificationCenter() {
        registerActivityLifecycleCallbacks(object : DefaultActivityLifecycleCallbacks() {
            override fun onActivityResumed(activity: Activity) {
                AppConfigurator.customAppComponent?.pushHandler()?.onActivityStarted(activity)
            }
        })
    }

    /**
     * Регистрирует слушатель аткивной активити
     */
    private fun registerActiveActivityListener() {
        registerActivityLifecycleCallbacks(object : DefaultActivityLifecycleCallbacks() {
            override fun onActivityResumed(activity: Activity) {
                activeActivityHolder.activity = activity
            }

            override fun onActivityStopped(activity: Activity) {
                activeActivityHolder.clearActivity()
            }
        })
    }

    private fun initPushEventListener() {
        PushClickProvider.pushEventListener = object : PushEventListener {
            override fun pushDismissListener(context: Context, intent: Intent) {
                Logger.i("Push notification dismissed")
            }

            override fun pushOpenListener(context: Context, intent: Intent) {
                Logger.i("Push notification open")
            }
        }
    }
}