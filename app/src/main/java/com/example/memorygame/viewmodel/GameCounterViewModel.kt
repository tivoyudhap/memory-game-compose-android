package com.example.memorygame.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorygame.entity.GameEntity
import com.example.memorygame.support.SECOND_PER_MINUTE
import com.example.memorygame.support.STATE_GAME_NOT_START
import com.example.memorygame.support.STATE_GAME_OVER
import com.example.memorygame.support.STATE_GAME_PAUSED
import com.example.memorygame.support.STATE_GAME_RUNNING
import com.example.memorygame.support.STATE_GAME_WINNER
import com.example.memorygame.support.TIME_MILLIS_PER_SECOND
import com.example.memorygame.support.TimerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameCounterViewModel @Inject constructor(private val timerFactory: TimerFactory): ViewModel() {

    private val counterMutableLiveData: MutableLiveData<GameEntity> = MutableLiveData()
    val counterLiveData: LiveData<GameEntity> = counterMutableLiveData

    private var gameCountDown: CountDownTimer? = null
    private var timeLeft: Long = 0

    fun destroy() {
        gameCountDown?.cancel()
        gameCountDown = null
    }

    fun startOrPause() {
        if (counterLiveData.value == null) {
            startGame()
        } else if (counterLiveData.value?.state == STATE_GAME_NOT_START) {
            startGame()
        } else if (counterLiveData.value?.state == STATE_GAME_RUNNING) {
            pauseGame()
        } else if (counterLiveData.value?.state == STATE_GAME_PAUSED) {
            resumeGame()
        } else if (counterLiveData.value?.state == STATE_GAME_WINNER) {
            resetGame()
        }
    }

    fun win() {
        counterMutableLiveData.value = GameEntity(
            state = STATE_GAME_WINNER,
            counter = timeLeft
        )

        gameCountDown?.cancel()
        gameCountDown = null
    }

    private fun startGame() {
        timeLeft = SECOND_PER_MINUTE * TIME_MILLIS_PER_SECOND
        gameCountDown = timerFactory.create(timeLeft, TIME_MILLIS_PER_SECOND, ::onTick, ::onFinish)
    }

    private fun onTick(millisUntilFinished: Long) {
        timeLeft = millisUntilFinished / TIME_MILLIS_PER_SECOND
        counterMutableLiveData.value = GameEntity(
            state = STATE_GAME_RUNNING,
            counter = timeLeft
        )
    }

    private fun onFinish() {
        counterMutableLiveData.value = GameEntity(
            state = STATE_GAME_OVER
        )
    }

    private fun pauseGame() {
        gameCountDown?.cancel()
        counterMutableLiveData.value = GameEntity(
            state = STATE_GAME_PAUSED,
            counter = timeLeft
        )
    }

    private fun resumeGame() {
        gameCountDown?.start()
        counterMutableLiveData.value = GameEntity(
            state = STATE_GAME_RUNNING,
            counter = timeLeft
        )
    }

    private fun resetGame() {
        timeLeft = 61
        counterMutableLiveData.value = GameEntity(
            state = STATE_GAME_NOT_START,
            counter = timeLeft
        )
    }
}