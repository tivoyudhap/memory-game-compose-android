package com.example.memorygame.support

import android.os.CountDownTimer

interface TimerFactory {
    fun create(timeMillis: Long, countDownInterval: Long, onTick: (Long) -> Unit, onFinish: () -> Unit): CountDownTimer
}