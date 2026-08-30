package io.paysky.paybutton.ui.fragment.cardsOperations

import android.os.Bundle
import io.paysky.paybutton.R

import io.paysky.paybutton.data.model.PaymentData
import io.paysky.paybutton.data.model.request.GetSessionRequest
import io.paysky.paybutton.data.model.request.ListSavedCardsRequest
import io.paysky.paybutton.data.model.response.CardItem
import io.paysky.paybutton.data.model.response.GetSessionResponse
import io.paysky.paybutton.data.model.response.ListSavedCardsResponse
import io.paysky.paybutton.data.network.ApiConnection
import io.paysky.paybutton.data.network.ApiResponseListener
import io.paysky.paybutton.ui.mvp.BasePresenter
import io.paysky.paybutton.util.AppConstant
import io.paysky.paybutton.util.AppUtils
import io.paysky.paybutton.util.HashGenerator


open class CardsPresenter<V : CardsView>(arguments: Bundle?, view: V) :
    BasePresenter<V>() {
    val cardsList = mutableListOf<CardItem>()
    val paymentData: PaymentData?

    init {
        attachView(view)
        paymentData = arguments?.getParcelable(AppConstant.BundleKeys.PAYMENT_DATA)
        getSessionToken()
    }

    private fun getSessionToken() {
        paymentData?.let { payment ->
            getSession(
                terminalId = payment.terminalId,
                merchantId = payment.merchantId,
                amount = payment.amount,
                customerId = payment.customerId
            )
        }
    }

    private fun getSession(
        terminalId: String,
        merchantId: String,
        amount: Double,
        customerId: String
    ) {
        // check internet.
        if (!view.isInternetAvailable) {
            view.showNoInternetDialog()
            return
        }
        view.showProgress()
        // create request body.
        val dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn()
        val secureHash = HashGenerator.encode(
            paymentData?.secureHashKey,
            dateTimeLocalTrxn,
            merchantId,
            terminalId
        )
        if (secureHash != null) {
            val request = GetSessionRequest(
                customerId = customerId,
                merchantId = merchantId,
                terminalId = terminalId,
                dateTimeLocalTrxn = dateTimeLocalTrxn,
                amount = amount,
                secureHash = secureHash
            )

            ApiConnection.getSession(
                request,
                object : ApiResponseListener<GetSessionResponse?> {
                    override fun onSuccess(response: GetSessionResponse?) {
                        handleGetSessionResponse(response)
                    }

                    override fun onFail(error: Throwable) {
                        error.printStackTrace()
                        view.dismissProgress()
                    }
                }
            )
        } else {
            view.showToastErrorAndFinish(R.string.something_went_wrong)
        }
    }


    private fun listCustomerCards() {
        paymentData?.let { payment ->
            // check internet.
            if (!view.isInternetAvailable) {
                view.showNoInternetDialog()
                return
            }
            // create request body.
            val dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn()
            val secureHash = HashGenerator.encode(
                paymentData.secureHashKey,
                dateTimeLocalTrxn,
                payment.merchantId,
                payment.terminalId
            )
            val request = ListSavedCardsRequest(
                sessionId = payment.customerSession,
                customerId = payment.customerId,
                amount = payment.amount,
                terminalId = payment.terminalId,
                merchantId = payment.merchantId,
                dateTimeLocalTrxn = dateTimeLocalTrxn,
                secureHash = secureHash
            )
            ApiConnection.listSavedCards(
                request,
                object : ApiResponseListener<ListSavedCardsResponse?> {
                    override fun onSuccess(response: ListSavedCardsResponse?) {
                        view.dismissProgress()
                        response?.let {
                            if (it.success) {
                                cardsList.clear()
                                cardsList.addAll(it.cardsLists)
                                setDefaultCardSelected()
                                view.showSavedCards(cardsList)
                                view.dismissProgress()
                            } else {
                                view.showToastError(response.message!!)
                                view.dismissProgress()

                            }
                        } ?: {
                            view.showToastError("Something went wrong")
                            view.dismissProgress()
                        }
                    }

                    override fun onFail(error: Throwable) {
                        error.printStackTrace()
                        view.dismissProgress()
                    }
                }
            )
        }
    }

    private fun handleGetSessionResponse(response: GetSessionResponse?) {
        view.dismissProgress()
        when {
            response == null -> view.showToastError("Something went wrong")
            response.success && !response.sessionId.isNullOrEmpty() -> {
                paymentData?.customerSession = response.sessionId
                listCustomerCards()
            }
            response.actionCode == ACTION_CODE_CUSTOMER_NOT_FOUND ->
                view.onCustomerNotFound()
            else -> view.showToastError(
                response.message ?: "Something went wrong"
            )
        }
    }

    private fun setDefaultCardSelected() {
        cardsList.find { it.isDefaultCard }?.isSelected = true
    }

    companion object {
        // Backend returns Success=false with this ActionCode when the
        // customer id sent in GetSessionForCustomerToken does not exist.
        private const val ACTION_CODE_CUSTOMER_NOT_FOUND = "01"
    }
}