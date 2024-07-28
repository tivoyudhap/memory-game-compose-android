package com.example.memorygame

import android.os.SystemClock.sleep
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.memorygame.support.TEST_TAG_GAMEPLAY_BUTTON
import com.example.memorygame.support.TEST_TAG_GAMEPLAY_IMAGE
import com.example.memorygame.support.TEST_TAG_LAZY_VERTICAL_COLUMN
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun beforeTest() {
        hiltAndroidRule.inject()
    }

    @Test
    fun givenInitialScreen_shouldShowTextShouldStart() {
        composeTestRule.apply {
            onNodeWithText("Press 'Play' button below to start").assertExists()
        }
    }

    @Test
    fun givenInitialScreen_shouldShow20ItemsRendered() {
        composeTestRule.apply {
            onNodeWithTag(TEST_TAG_LAZY_VERTICAL_COLUMN)
                .onChildren()
                .assertCountEquals(20)
        }
    }

    @Test
    fun givenInitialScreen_shouldShowBottomButtonWithStartAsset() {
        composeTestRule.apply {
            onNodeWithTag(TEST_TAG_GAMEPLAY_IMAGE, useUnmergedTree = true)
                .assert(hasDrawable(R.drawable.rounded_not_started))
        }
    }

    @Test
    fun givenInitialScreen_allOfCardShouldNotFlipped() {
        composeTestRule.apply {
            val cardCount = onNodeWithTag(TEST_TAG_LAZY_VERTICAL_COLUMN)
                .onChildren()
                .fetchSemanticsNodes()
                .size

            for (a in 0 until cardCount) {
                onNodeWithTag(TEST_TAG_LAZY_VERTICAL_COLUMN)
                    .onChildAt(a)
                    .assert(isStateFlipped(false))
            }
        }
    }

    @Test
    fun givenFirstCardIsClicked_stateFlipShouldOnlyInFirstCard() {
        composeTestRule.apply {
            val cardCount = onNodeWithTag(TEST_TAG_LAZY_VERTICAL_COLUMN)
                .onChildren()
                .fetchSemanticsNodes()
                .size

            onNodeWithTag(TEST_TAG_GAMEPLAY_BUTTON)
                .performClick()

            sleep(1000)

            onNodeWithTag(TEST_TAG_LAZY_VERTICAL_COLUMN)
                .onChildAt(0)
                .performClick()

            for (a in 0 until cardCount) {
                onNodeWithTag(TEST_TAG_LAZY_VERTICAL_COLUMN)
                    .onChildAt(a)
                    .assert(isStateFlipped(a == 0))
            }
        }
    }
}