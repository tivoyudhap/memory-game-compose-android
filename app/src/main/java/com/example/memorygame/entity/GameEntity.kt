package com.example.memorygame.entity

import com.example.memorygame.support.STATE_GAME_PAUSED

data class GameEntity(
    var state: String = STATE_GAME_PAUSED,
    var counter: Long = 0
)