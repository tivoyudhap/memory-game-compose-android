package com.example.memorygame.provider

import com.example.memorygame.support.TimerFactory
import com.example.memorygame.support.TimerFactoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ModuleProvider {

    @Provides
    @Singleton
    fun provideTimer(): TimerFactory = TimerFactoryImpl()
}