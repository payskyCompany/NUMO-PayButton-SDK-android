package io.paysky.paybutton.ui.fragment.cardsOperations

import io.paysky.paybutton.data.model.response.CardItem
import io.paysky.paybutton.ui.mvp.BaseView

interface CardsView : BaseView {
    fun showToastError(message: String)
    fun showToastErrorAndFinish(error: Int)
    fun showSavedCards(cardsLists: List<CardItem>)
    fun onCustomerNotFound()
}