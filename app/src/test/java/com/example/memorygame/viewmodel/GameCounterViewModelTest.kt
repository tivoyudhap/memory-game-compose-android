package com.example.memorygame.viewmodel

import android.os.CountDownTimer
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.memorygame.entity.GameEntity
import com.example.memorygame.helper.MockitoHelper.anyObject
import com.example.memorygame.helper.whenever
import com.example.memorygame.support.STATE_GAME_NOT_START
import com.example.memorygame.support.STATE_GAME_OVER
import com.example.memorygame.support.STATE_GAME_PAUSED
import com.example.memorygame.support.STATE_GAME_RUNNING
import com.example.memorygame.support.STATE_GAME_WINNER
import com.example.memorygame.support.TimerFactory
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class GameCounterViewModelTest {

    private lateinit var viewModel: GameCounterViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var timerFactory: TimerFactory

    @Mock
    private lateinit var countDownTimer: CountDownTimer

    @Mock
    private lateinit var observer: Observer<GameEntity>

    @Before
    fun beforeTest() {
        whenever(timerFactory.create(anyLong(), anyLong(), anyObject(), anyObject())).thenReturn(countDownTimer)

        viewModel = GameCounterViewModel(timerFactory)
    }

    @Test
    fun `GIVEN initiate state, LiveData MUST be null`() {
        assert(viewModel.counterLiveData.value == null)
    }

    @Test
    fun `GIVEN start game function is called, state MUST be STATE_GAME_RUNNING`() {
        viewModel.counterLiveData.observeForever(observer)

        doAnswer {
            val onTickCallback = it.arguments[2] as (Long) -> Unit
            onTickCallback(1000)
            null
        }.whenever(timerFactory).create(anyLong(), anyLong(), anyObject(), anyObject())

        viewModel.startOrPause()

        verify(observer).onChanged(GameEntity(state = STATE_GAME_RUNNING, counter = 1))
    }

    @Test
    fun `GIVEN timer is finished, state MUST be STATE_GAME_OVER`() {
        viewModel.counterLiveData.observeForever(observer)

        doAnswer {
            val onFinishCallback = it.arguments[3] as () -> Unit
            onFinishCallback()
            null
        }.whenever(timerFactory).create(anyLong(), anyLong(), anyObject(), anyObject())

        viewModel.startOrPause()

        verify(observer).onChanged(GameEntity(state = STATE_GAME_OVER, counter = 0))
    }

    @Test
    fun `GIVEN pause game, state MUST be STATE_GAME_PAUSED`() {
        viewModel.counterLiveData.observeForever(observer)

        doAnswer {
            val onTickCallback = it.arguments[2] as (Long) -> Unit
            onTickCallback(1000)
            null
        }.whenever(timerFactory).create(anyLong(), anyLong(), anyObject(), anyObject())

        viewModel.startOrPause()

        viewModel.startOrPause()

        verify(observer).onChanged(GameEntity(state = STATE_GAME_PAUSED, counter = 1))
    }


    @Test
    fun `GIVEN winning game, state MUST be STATE_GAME_WINNER`() {
        viewModel.counterLiveData.observeForever(observer)

        viewModel.win()

        verify(observer).onChanged(GameEntity(state = STATE_GAME_WINNER, counter = 0))
    }

    @Test
    fun `GIVEN call startGame after winning game, state MUST be STATE_GAME_NOT_START`() {
        viewModel.counterLiveData.observeForever(observer)

        viewModel.win()

        viewModel.startOrPause()

        verify(observer).onChanged(GameEntity(state = STATE_GAME_NOT_START, counter = 61))
    }

    @Test
    fun `GIVEN state is STATE_GAME_NOT_START then startOrPause function is called, state must BE STATE_GAME_RUNNING`() {
        viewModel.counterLiveData.observeForever(observer)

        doAnswer {
            val onTickCallback = it.arguments[2] as (Long) -> Unit
            onTickCallback(1000)
            null
        }.whenever(timerFactory).create(anyLong(), anyLong(), anyObject(), anyObject())

        viewModel.win()

        viewModel.startOrPause()

        viewModel.startOrPause()

        verify(observer).onChanged(GameEntity(state = STATE_GAME_RUNNING, counter = 1))
    }

    @Test
    fun `GIVEN state is STATE_GAME_PAUSED then call startOrPause(), state MUST be STATE_GAME_RUNNING`() {
        viewModel.counterLiveData.observeForever(observer)

        doAnswer {
            val onTickCallback = it.arguments[2] as (Long) -> Unit
            onTickCallback(1000)
            null
        }.whenever(timerFactory).create(anyLong(), anyLong(), anyObject(), anyObject())

        viewModel.startOrPause()

        viewModel.startOrPause()

        viewModel.startOrPause()

        assert(viewModel.counterLiveData.value?.state == STATE_GAME_RUNNING)
        assert(viewModel.counterLiveData.value?.counter == 1L)
    }
}