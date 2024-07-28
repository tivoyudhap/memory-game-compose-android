package com.example.memorygame

import androidx.compose.ui.test.SemanticsMatcher
import com.example.memorygame.support.TestTag

fun hasDrawable(resourceId: Int): SemanticsMatcher {
    return SemanticsMatcher.expectValue(TestTag.imageResource, resourceId)
}

fun isStateFlipped(assertFlip: Boolean): SemanticsMatcher {
    return SemanticsMatcher.expectValue(TestTag.isFlipped, assertFlip)
}