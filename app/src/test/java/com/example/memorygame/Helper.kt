package com.example.memorygame

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing
import org.mockito.stubbing.Stubber

fun <T>whenever(methodCall: T): OngoingStubbing<T> = Mockito.`when`(methodCall)

inline fun <reified T> Stubber.whenever(methodCall: T): T = `when`(methodCall)