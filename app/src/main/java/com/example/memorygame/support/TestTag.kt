package com.example.memorygame.support

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics

object TestTag {
    val imageResource = SemanticsPropertyKey<Int>("ImageResource")
    val isFlipped = SemanticsPropertyKey<Boolean>("isCardFlipped")
}

fun Modifier.testTag(resourceId: Int) = semantics {
    this[TestTag.imageResource] = resourceId
}

fun Modifier.cardStateTag(isFlipped: Boolean) = semantics {
    this[TestTag.isFlipped] = isFlipped
}