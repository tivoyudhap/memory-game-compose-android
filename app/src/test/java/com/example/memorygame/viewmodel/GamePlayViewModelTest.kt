package com.example.memorygame.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.memorygame.entity.CardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GamePlayViewModelTest {

    private lateinit var viewModel: GamePlayViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun beforeTest() {
        Dispatchers.setMain(dispatcher)
        viewModel = GamePlayViewModel()
    }

    @Test
    fun `GIVEN all card initial list, SHOULD contains 20 images and each have pair`() {
        viewModel.generateInitialImageMapping()

        var noPairCardDetected = false
        viewModel.initiateCardLiveData.value?.forEach { cardEntity ->
            noPairCardDetected = viewModel.initiateCardLiveData.value?.filter { it.resourceId == cardEntity.resourceId }?.size != 2
        }

        assert(viewModel.initiateCardLiveData.value?.size == 20)
        assert(!noPairCardDetected)
    }

    @Test
    fun `GIVEN onFlip is called, SHOULD flip card if card is not flipped and first card`() {
        viewModel.generateInitialImageMapping()

        val randomFirstCard = viewModel.initiateCardLiveData.value?.first() ?: CardEntity()

        viewModel.onFlip(randomFirstCard)

        assert(viewModel.initiateCardLiveData.value?.first { it.id == randomFirstCard.id }?.isFlipped == true)
    }

    @Test
    fun `GIVEN onFlip is called twice with same card, SHOULD mark 2 data as complete`() {
        viewModel.generateInitialImageMapping()

        val randomFirstCard = viewModel.initiateCardLiveData.value?.first() ?: CardEntity()
        val pairedCard = viewModel.initiateCardLiveData.value?.last { it.resourceId == randomFirstCard.resourceId } ?: CardEntity(id = 99)

        viewModel.onFlip(randomFirstCard)

        viewModel.onFlip(pairedCard)

        assert(viewModel.initiateCardLiveData.value?.filter { it.resourceId == randomFirstCard.resourceId }?.filter { it.isComplete }?.size == 2)
    }

    @Test
    fun `GIVEN complete card is flipped, SHOULD call allCardFlipped live data`() {
        viewModel.generateInitialImageMapping()

        val distinctList = viewModel.initiateCardLiveData.value?.distinctBy { it.resourceId } ?: listOf()

        distinctList.forEach { cardEntity ->
            viewModel.onFlip(cardEntity)

            val otherCard = viewModel.initiateCardLiveData.value?.filter { it.resourceId == cardEntity.resourceId && it.id != cardEntity.id }?.first() ?: CardEntity()

            viewModel.onFlip(otherCard)
        }

        assert(viewModel.allCardFlippedLiveData.value == true)
    }

    @Test
    fun `GIVEN user select different card, SHOULD reset both card to isFlipped false and not mark as complete`() = runTest {
        viewModel.generateInitialImageMapping()

        val distinctList = viewModel.initiateCardLiveData.value?.distinctBy { it.resourceId } ?: listOf()

        val firstCard = distinctList.first()
        val secondCard = distinctList.last()

        viewModel.onFlip(firstCard)
        viewModel.onFlip(secondCard)

        advanceTimeBy(2000)

        viewModel.initiateCardLiveData.value?.first { it.id == firstCard.id }?.let {
            assert(!it.isFlipped)
            assert(!it.isComplete)
        }

        viewModel.initiateCardLiveData.value?.first { it.id == secondCard.id }?.let {
            assert(!it.isFlipped)
            assert(!it.isComplete)
        }
    }

    @Test
    fun `GIVEN call onFlip with entity isFlipped is true, SHOULD do nothing`() {
        viewModel.generateInitialImageMapping()

        val currentList = viewModel.initiateCardLiveData.value ?: listOf()

        val firstCard = currentList.first()

        viewModel.onFlip(firstCard)

        val updatedList = viewModel.initiateCardLiveData.value ?: listOf()

        viewModel.onFlip(firstCard)

        var isFlippedMatch = false
        viewModel.initiateCardLiveData.value?.forEachIndexed { index, cardEntity ->
            isFlippedMatch = updatedList[index].isFlipped == cardEntity.isFlipped
        }

        assert(isFlippedMatch)
    }

    @Test
    fun afterTest() {
        Dispatchers.shutdown()
    }
}