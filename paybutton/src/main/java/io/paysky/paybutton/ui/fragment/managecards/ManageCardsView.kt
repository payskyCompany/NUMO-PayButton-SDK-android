package io.paysky.paybutton.ui.fragment.managecards

import io.paysky.paybutton.ui.fragment.cardsOperations.CardsView

interface ManageCardsView : CardsView {
    fun setCardAsDefault(position: Int)
    fun deleteCard(position: Int)
    fun revertDefaultSelect(position: Int)
}