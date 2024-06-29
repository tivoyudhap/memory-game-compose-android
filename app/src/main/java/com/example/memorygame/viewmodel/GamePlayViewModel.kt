package com.example.memorygame.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memorygame.R
import com.example.memorygame.entity.CardEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GamePlayViewModel: ViewModel() {

    private val imageMapping: MutableMap<Int, Int> = mutableMapOf(
        1 to R.drawable.ic_banana,
        2 to R.drawable.ic_coconut,
        3 to R.drawable.ic_orange,
        4 to R.drawable.ic_mangosteen,
        5 to R.drawable.ic_papaya,
        6 to R.drawable.ic_rambutan,
        7 to R.drawable.ic_watermelon,
        8 to R.drawable.ic_grape,
        9 to R.drawable.ic_melon,
        10 to R.drawable.ic_cherry
    )

    private var selectedCardEntity: CardEntity? = null

    private val listOfCardMutableLiveData: MutableLiveData<List<CardEntity>> = MutableLiveData()
    val initiateCardLiveData: LiveData<List<CardEntity>> = listOfCardMutableLiveData

    private val allCardFlippedMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val allCardFlippedLiveData: LiveData<Boolean> = allCardFlippedMutableLiveData

    fun generateInitialImageMapping() {
        val list: MutableList<CardEntity> = mutableListOf()
        for (a in 0 until 20) {
            list.add(
                CardEntity(
                    id = a,
                    resourceId = imageMapping.getValue(if (a + 1 > 10) a - 9 else a + 1)
                )
            )
        }

        listOfCardMutableLiveData.value = list.shuffled()
    }

    fun onFlip(entity: CardEntity) {
        if (!entity.isFlipped) {
            val updatedList = listOfCardMutableLiveData.value?.map { if (it.id == entity.id) it.copy(isFlipped = !entity.isFlipped) else it }
            listOfCardMutableLiveData.value = updatedList?.toMutableList()
            isCardMatch(entity)
            if (isAllCardMatch()) {
                allCardFlippedMutableLiveData.value = true
            }
        }
    }

    private fun isCardMatch(selectedCardEntity: CardEntity) {
        if (this.selectedCardEntity == null) {
            this.selectedCardEntity = selectedCardEntity
        } else {
            if (selectedCardEntity.resourceId != this.selectedCardEntity?.resourceId) {
                delayBeforeFlip(selectedCardEntity)
            } else {
                markCardAsComplete(selectedCardEntity)
            }
        }
    }

    private fun markCardAsComplete(selectedCardEntity: CardEntity) {
        listOfCardMutableLiveData.value = listOfCardMutableLiveData.value?.map {
            if ((it.id == selectedCardEntity.id) or (it.id == this@GamePlayViewModel.selectedCardEntity?.id)) {
                it.copy(isComplete = true, isFlipped = true)
            } else {
                it
            }
        }

        this@GamePlayViewModel.selectedCardEntity = null
    }

    private fun delayBeforeFlip(selectedCardEntity: CardEntity) {
        viewModelScope.launch {
            val savedSelectedCardEntity = this@GamePlayViewModel.selectedCardEntity?.copy()
            delay(2000)
            listOfCardMutableLiveData.value = listOfCardMutableLiveData.value?.map {
                if ((it.id == selectedCardEntity.id) or (it.id == savedSelectedCardEntity?.id)) {
                    it.copy(isFlipped = false)
                } else {
                    it
                }
            }

            this@GamePlayViewModel.selectedCardEntity = null
        }
    }

    private fun isAllCardMatch(): Boolean =
        listOfCardMutableLiveData.value?.none { !it.isFlipped } == true
}