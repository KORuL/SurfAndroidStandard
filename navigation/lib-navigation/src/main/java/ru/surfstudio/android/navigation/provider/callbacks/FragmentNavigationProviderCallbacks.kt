package ru.surfstudio.android.navigation.provider.callbacks

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import ru.surfstudio.android.navigation.command.fragment.base.FragmentNavigationCommand
import ru.surfstudio.android.navigation.provider.container.FragmentNavigationContainer
import ru.surfstudio.android.navigation.provider.holder.FragmentNavigationHolder
import ru.surfstudio.android.navigation.provider.FragmentNavigationProvider
import ru.surfstudio.android.navigation.navigator.fragment.FragmentNavigator
import ru.surfstudio.android.navigation.navigator.fragment.tab.TabFragmentNavigator
import ru.surfstudio.android.navigation.provider.container.TabFragmentNavigationContainer

/**
 * Fragment navigation entities provider.
 *
 * It is based on a fragment lifecycle callbacks and
 * can be used to provide navigation entities for current visible activity.
 *
 * Used in activity scope (created once for each new activity).
 */
open class FragmentNavigationProviderCallbacks(
        activity: AppCompatActivity,
        savedState: Bundle?
) : FragmentManager.FragmentLifecycleCallbacks(), FragmentNavigationProvider {

    /**
     * Holders with fragment navigators for each [FragmentNavigationContainer]
     */
    private val navigationHolders = hashMapOf<String, FragmentNavigationHolder>()

    /**
     * List of active (created and not yet destroyed) fragments
     */
    private val activeFragments = mutableListOf<Fragment>()

    init {
        addZeroLevelHolder(activity, savedState)
    }

    override fun provide(sourceTag: String?): FragmentNavigationHolder {
        val sourceFragment = if (sourceTag.isNullOrEmpty()) {
            null
        } else {
            activeFragments.find { getFragmentId(it).contains(sourceTag) }
        }
        val navigationHolder = obtainFragmentHolderRecursive(sourceFragment)
                ?: navigationHolders.values.firstOrNull()
        return requireNotNull(navigationHolder) { NO_NAVIGATION_HOLDER_ERROR }
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        Log.d("111111 Create", "Fragment=$f, Manager=$fm")

        val id = getFragmentId(f)
        val hasFragmentWithSameId = activeFragments.any { getFragmentId(it) == id }
        if (hasFragmentWithSameId) error("You must specify unique tag for each fragment! Tag=$id")

        activeFragments.add(f)

        if (f !is FragmentNavigationContainer) return
        addHolder(id, f, f.childFragmentManager, savedInstanceState)
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
        val id = getFragmentId(f)

        navigationHolders[id]?.run {
            fragmentNavigator.onSaveState(outState)
        }
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        navigationHolders.remove(getFragmentId(f))
        activeFragments.remove(f)
    }

    fun onActivitySaveState(outState: Bundle?) {
        navigationHolders[FragmentNavigationCommand.ACTIVITY_NAVIGATION_TAG]?.run {
            fragmentNavigator.onSaveState(outState)
        }
    }

    /**
     * Добавление холдера на 0-ой уровень, т.е. на уровень Activity, которая управляет фрагментами.
     */
    private fun addZeroLevelHolder(activity: FragmentActivity, savedInstanceState: Bundle?) {
        if (activity !is FragmentNavigationContainer) return

        val fragmentManager = activity.supportFragmentManager
        val id = FragmentNavigationCommand.ACTIVITY_NAVIGATION_TAG

        addHolder(id, activity, fragmentManager, savedInstanceState)
    }

    /**
     * Adds holder with [FragmentNavigator] on a desired level.
     *
     * @param id unique holder id
     * @param container navigation container, which will hold the navigation
     * @param fm manager that will be used in navigators to execute commands
     * @param savedInstanceState state that is used to restore backstack
     */
    private fun addHolder(
            id: String,
            container: FragmentNavigationContainer,
            fm: FragmentManager,
            savedInstanceState: Bundle?
    ) {
        val oldHolder = navigationHolders[id]
        if (oldHolder != null) error(CONTAINER_TAG_IS_NOT_UNIQUE_ERROR)
        navigationHolders[id] = createHolder(id, container, fm, savedInstanceState)
    }

    protected open fun createHolder(
            id: String,
            container: FragmentNavigationContainer,
            fm: FragmentManager,
            savedInstanceState: Bundle?
    ): FragmentNavigationHolder {

        val containerId = container.containerId
        val isTabContainer = container is TabFragmentNavigationContainer

        val fragmentNavigator = if (isTabContainer) {
            TabFragmentNavigator(fm, containerId, savedInstanceState)
        } else {
            FragmentNavigator(fm, containerId, savedInstanceState)
        }

        return FragmentNavigationHolder(fragmentNavigator)
    }

    private fun obtainFragmentHolderRecursive(fragment: Fragment?): FragmentNavigationHolder? {
        if (fragment == null) return null
        val id = getFragmentId(fragment)
        val fragmentHolder = navigationHolders[id]
        return fragmentHolder ?: obtainFragmentHolderRecursive(fragment.parentFragment)
    }


    private fun getFragmentId(fragment: Fragment): String {
        return fragment.tag ?: error(NO_FRAGMENT_ID_ERROR)
    }

    private companion object {
        const val NO_NAVIGATION_HOLDER_ERROR =
                "There's no navigation holders in FragmentNavigationProvider! " +
                        "If your Activity is hosting fragments, " +
                        "you should inherit it from FragmentNavigationContainer."

        const val CONTAINER_TAG_IS_NOT_UNIQUE_ERROR = "You must specify unique tag for each FragmentNavigationContainer!"
        const val NO_FRAGMENT_ID_ERROR = "Fragment tag must always be specified!"
    }
}