package com.example.memorygame.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memorygame.R
import com.example.memorygame.entity.CardEntity

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

    private val listOfCard: MutableList<CardEntity> = mutableListOf()
    private var selectedCardEntity: CardEntity? = null

    private val initiateCardMutableLiveData: MutableLiveData<MutableList<CardEntity>> = MutableLiveData()
    val initiateCardLiveData: LiveData<MutableList<CardEntity>> = initiateCardMutableLiveData

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

        listOfCard.addAll(list.shuffled())

        initiateCardMutableLiveData.value = listOfCard
    }

    fun onFlip(entity: CardEntity) {
        if (!entity.isFlipped) {
            listOfCard.first { it.id == entity.id }.isFlipped = true
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
                listOfCard.first { it.id == selectedCardEntity.id }.isFlipped = false
                listOfCard.first { it.id == this.selectedCardEntity?.id }.isFlipped = false
            }

            this.selectedCardEntity = null
        }

        initiateCardMutableLiveData.value = listOfCard
    }

    private fun isAllCardMatch(): Boolean = listOfCard.filter { !it.isFlipped }.isEmpty()
}