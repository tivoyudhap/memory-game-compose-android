package com.example.memorygame.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorygame.entity.GameEntity
import com.example.memorygame.support.SECOND_PER_MINUTE
import com.example.memorygame.support.STATE_GAME_OVER
import com.example.memorygame.support.STATE_GAME_PAUSED
import com.example.memorygame.support.STATE_GAME_RUNNING
import com.example.memorygame.support.TIME_MILLIS_PER_SECOND

class GameCounterViewModel: ViewModel() {

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
        } else if (counterLiveData.value?.state == STATE_GAME_RUNNING) {
            pauseGame()
        } else if (counterLiveData.value?.state == STATE_GAME_PAUSED) {
            resumeGame()
        }
    }

    private fun startGame() {
        timeLeft = SECOND_PER_MINUTE * TIME_MILLIS_PER_SECOND

        gameCountDown = object : CountDownTimer(timeLeft, TIME_MILLIS_PER_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished / TIME_MILLIS_PER_SECOND
                counterMutableLiveData.value = GameEntity(
                    state = STATE_GAME_RUNNING,
                    counter = timeLeft
                )
            }

            override fun onFinish() {
                counterMutableLiveData.value = GameEntity(
                    state = STATE_GAME_OVER
                )
            }
        }.start()
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
}