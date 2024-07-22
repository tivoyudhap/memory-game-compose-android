package com.example.memorygame.support

import android.os.CountDownTimer

class TimerFactoryImpl : TimerFactory {
    override fun create(
        timeMillis: Long,
        countDownInterval: Long,
        onTick: (Long) -> Unit,
        onFinish: () -> Unit
    ): CountDownTimer = object : CountDownTimer(timeMillis, TIME_MILLIS_PER_SECOND) {
        override fun onTick(millisUntilFinished: Long) {
            onTick(millisUntilFinished)
        }

        override fun onFinish() {
            onFinish()
        }
    }.start()
}