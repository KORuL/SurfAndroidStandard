package ru.surfstudio.android.mvp.widget

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
import ru.surfstudio.android.core.ui.activity.CoreActivityInterface
import ru.surfstudio.android.core.ui.event.ScreenEventDelegateManager
import ru.surfstudio.android.core.ui.state.ActivityScreenState
import ru.surfstudio.android.core.ui.state.LifecycleStage
import ru.surfstudio.android.mvp.widget.delegate.WidgetViewDelegate
import ru.surfstudio.android.mvp.widget.event.StageResolver
import ru.surfstudio.android.mvp.widget.event.WidgetLifecycleManager
import ru.surfstudio.android.mvp.widget.event.delegate.WidgetScreenEventDelegateManager
import ru.surfstudio.android.mvp.widget.state.WidgetScreenState
import ru.surfstudio.android.mvp.widget.view.CoreWidgetViewInterface

@RunWith(PowerMockRunner::class)
class LifecycleManagerTest {


    lateinit var parentState: ActivityScreenState

    lateinit var widgetLifecycleManager: WidgetLifecycleManager

    lateinit var screenState: WidgetScreenState

    lateinit var stageResolver: StageResolver

    private var actualStage: LifecycleStage? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        parentState = ActivityScreenState()
        parentState.onCreate(mock(FragmentActivity::class.java), mock(CoreActivityInterface::class.java), Bundle.EMPTY)

        screenState = WidgetScreenState(parentState)
        widgetLifecycleManager = WidgetLifecycleManager(screenState, parentState, mock(WidgetScreenEventDelegateManager::class.java), mock(ScreenEventDelegateManager::class.java))
        widgetLifecycleManager.onCreate(mock(View::class.java), mock(CoreWidgetViewInterface::class.java), mock(WidgetViewDelegate::class.java))

        stageResolver = StageResolver(screenState, parentState) {
            actualStage = it
            widgetLifecycleManager.applyStage(it)
            println("TEST_INFO actualStage = $actualStage | screenStage = ${screenState.lifecycleStage} ")
        }


    }

    @Test
    fun testSimpleCase() {
        parentState.onResume()
        //CREATED - уже выставленное состояние
        with(stageResolver) {
            pushState(LifecycleStage.VIEW_READY)
            assert(actualStage == LifecycleStage.RESUMED, { "actualStage = $actualStage | expected = ${LifecycleStage.RESUMED} | screenStage = ${screenState.lifecycleStage} " })
            pushState(LifecycleStage.PAUSED)
            pushState(LifecycleStage.STOPPED)
            parentState.onDestroyView()
            pushState(LifecycleStage.VIEW_DESTROYED)
            parentState.onCompletelyDestroy()
            pushState(LifecycleStage.DESTROYED)

            assert(actualStage == LifecycleStage.DESTROYED, { "actualStage = $actualStage | expected = ${LifecycleStage.DESTROYED} | screenStage = ${screenState.lifecycleStage} " })
        }
    }

    @Test
    fun testSimpleCase2() {
        parentState.onResume()
        //CREATED - уже выставленное состояние

        with(stageResolver) {
            pushState(LifecycleStage.VIEW_READY) //запустит STARTED/RESUMED
            pushState(LifecycleStage.PAUSED)
            pushState(LifecycleStage.RESUMED)
            pushState(LifecycleStage.STOPPED) //не пройдет, так как запрещен переход
            pushState(LifecycleStage.PAUSED)
            pushState(LifecycleStage.STOPPED)
            pushState(LifecycleStage.STARTED)
            pushState(LifecycleStage.RESUMED)
            pushState(LifecycleStage.VIEW_DESTROYED) //сделали детач
            pushState(LifecycleStage.VIEW_READY)
            assert(actualStage == LifecycleStage.RESUMED)
        }

    }

    @Test
    fun testAnyExceptVrAfterCreated() {
        actualStage = LifecycleStage.CREATED
        val stages = listOf(LifecycleStage.STARTED, LifecycleStage.RESUMED, LifecycleStage.PAUSED, LifecycleStage.STOPPED, LifecycleStage.VIEW_DESTROYED)
        stages.forEach {
            stageResolver.pushState(it)
            assert(actualStage == LifecycleStage.CREATED)
        }

        stageResolver.pushState(LifecycleStage.DESTROYED)
        assert(actualStage == LifecycleStage.DESTROYED)
    }

    @Test
    fun testDestroyAfterResume() {
        parentState.onResume()
        val stages = listOf(LifecycleStage.VIEW_READY)
        stages.forEach {
            stageResolver.pushState(it)
            assert(actualStage == LifecycleStage.RESUMED, { "actualStage = $actualStage | expected = ${LifecycleStage.RESUMED} | screenStage = ${screenState.lifecycleStage} " })
        }

        //резко убиваем экран
        println("destroy screen")
        parentState.onCompletelyDestroy()
        stageResolver.pushState(LifecycleStage.DESTROYED)
        assert(actualStage == LifecycleStage.DESTROYED)
    }

    @Test
    fun testDestroyAfterResumeManual() {
        parentState.onResume()
        val stages = listOf(LifecycleStage.VIEW_READY)
        stages.forEach {
            stageResolver.pushState(it)
            assert(actualStage == LifecycleStage.RESUMED, { "actualStage = $actualStage | expected = ${LifecycleStage.RESUMED} | screenStage = ${screenState.lifecycleStage} " })
        }

        //резко убиваем экран
        println("destroy screen")
        stageResolver.pushState(LifecycleStage.DESTROYED)
        assert(actualStage == LifecycleStage.DESTROYED)
    }

    @Test
    fun testLikeRv() {
        parentState.onResume()
        val stages = listOf(LifecycleStage.VIEW_READY)
        stages.forEach {
            stageResolver.pushState(it)
            assert(actualStage == LifecycleStage.RESUMED, { "actualStage = $actualStage | expected = $it | screenStage = ${screenState.lifecycleStage} " })
        }


        println("detach view")
        stageResolver.pushState(LifecycleStage.VIEW_DESTROYED)
        assert(actualStage == LifecycleStage.VIEW_DESTROYED)

        println("attach")
        stageResolver.pushState(LifecycleStage.VIEW_READY)
        assert(actualStage == LifecycleStage.RESUMED)

        println("destroy screen")
        parentState.onDestroy()
        stageResolver.pushState(LifecycleStage.DESTROYED)
        assert(actualStage == LifecycleStage.DESTROYED)
    }

    @Test
    fun testPausedResumed() {
        parentState.onResume()
        stageResolver.pushState(LifecycleStage.VIEW_READY)

        parentState.onPause()
        stageResolver.pushState(LifecycleStage.PAUSED)

        parentState.onResume()
        stageResolver.pushState(LifecycleStage.RESUMED)

        //не применяется
        stageResolver.pushState(LifecycleStage.STARTED)
        assert(actualStage == LifecycleStage.RESUMED)
    }

    @Test
    fun testReadyStartedPausedResumed() {
        parentState.onViewReady()
        stageResolver.pushState(LifecycleStage.VIEW_READY)

        parentState.onStart()
        stageResolver.pushState(LifecycleStage.STARTED)


        parentState.onPause()
        stageResolver.pushState(LifecycleStage.PAUSED)

        parentState.onResume()
        stageResolver.pushState(LifecycleStage.RESUMED)

        stageResolver.pushState(LifecycleStage.STARTED)
        assert(actualStage == LifecycleStage.RESUMED)
    }

    @Test
    fun testDestroyedAfterReady() {
        stageResolver.pushState(LifecycleStage.VIEW_READY)
        parentState.onCompletelyDestroy()
        stageResolver.pushState(LifecycleStage.DESTROYED)

        assert(actualStage == LifecycleStage.DESTROYED)
    }

    @Test(expected = AssertionError::class)
    fun testExceptionResumedWithoutStarted() {
        stageResolver.pushState(LifecycleStage.VIEW_READY)
        parentState.onResume()
        stageResolver.pushState(LifecycleStage.RESUMED)
        assert(actualStage == LifecycleStage.RESUMED)
    }

    @Test
    fun testSomeCases() {
        //текущее состояние виджета - CREATED
        actualStage = LifecycleStage.CREATED

        parentState.onResume()

        with(stageResolver) {
            pushState(LifecycleStage.RESUMED) //не примениться, запрещенный переход
            assert(actualStage == LifecycleStage.CREATED)

            pushState(LifecycleStage.VIEW_READY)
            assert(actualStage == LifecycleStage.RESUMED)

            parentState.onPause()
            pushState(LifecycleStage.PAUSED)
            //подумать над кейсом ниже
            pushState(LifecycleStage.VIEW_DESTROYED) //сделали детач
            assert(actualStage == LifecycleStage.VIEW_DESTROYED)

            parentState.onResume()
            pushState(LifecycleStage.VIEW_READY)
            assert(actualStage == LifecycleStage.RESUMED)
        }
    }

    @Test(expected = AssertionError::class)
    fun testExceptionAttachedAfterDestroyed() {
        parentState.onResume()
        //CREATED - уже выставленное состояние

        with(stageResolver) {
            pushState(LifecycleStage.DESTROYED)
            pushState(LifecycleStage.VIEW_READY)
            assert(actualStage == LifecycleStage.RESUMED)
        }
    }
}