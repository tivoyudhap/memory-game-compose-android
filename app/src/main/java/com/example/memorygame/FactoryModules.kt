package com.example.memorygame

import com.example.memorygame.support.TimerFactory
import com.example.memorygame.support.TimerFactoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FactoryModules {

    @Provides
    fun provideTimerFactory(): TimerFactory = TimerFactoryImpl()
}