package io.paysky.paybutton.ui.fragment.paymentprocessing

import android.os.Bundle
import android.util.Log
import io.paysky.paybutton.data.model.PaymentData
import io.paysky.paybutton.data.model.SuccessfulCardTransaction
import io.paysky.paybutton.data.model.TokenizedCardPaymentParameters
import io.paysky.paybutton.data.model.request.ManualPaymentRequest
import io.paysky.paybutton.data.model.response.ManualPaymentResponse
import io.paysky.paybutton.data.network.ApiConnection
import io.paysky.paybutton.data.network.ApiLinks
import io.paysky.paybutton.data.network.ApiResponseListener
import io.paysky.paybutton.exception.TransactionException
import io.paysky.paybutton.ui.mvp.BasePresenter
import io.paysky.paybutton.util.AppConstant
import io.paysky.paybutton.util.AppUtils
import io.paysky.paybutton.util.HashGenerator
import io.paysky.paybutton.util.TransactionManager

class PaymentProcessingPresenter(
    arguments: Bundle?,
    view: PaymentProcessingView
) :
    BasePresenter<PaymentProcessingView>() {
    private val paymentData: PaymentData?
    private val tokenizedCard: TokenizedCardPaymentParameters?

    init {
        paymentData = arguments?.getParcelable(AppConstant.BundleKeys.PAYMENT_DATA)
        tokenizedCard = arguments?.getParcelable(AppConstant.BundleKeys.TOKENIZED_CARD)

        TransactionManager.setTransactionType(TransactionManager.TransactionType.MANUAL)
        attachView(view)

        makeTokenizedCardPayment()
    }

    private fun makeTokenizedCardPayment() {
        paymentData?.let { payment ->
            tokenizedCard?.let { tokenizedCardParams ->
                makeTokenizedPayment(
                    secureHash = payment.secureHashKey,
                    currencyCode = payment.currencyCode,
                    payAmount = payment.amountFormatted,
                    merchantId = payment.merchantId,
                    terminalId = payment.terminalId,
                    ccv = tokenizedCardParams.cvv,
                    cardId = tokenizedCardParams.TokenCardId,
                    customerId = payment.customerId,
                    customerSessionId = payment.customerSession
                )
            } ?: run {
                Log.d("Make Payment", "makeTokenizedCardPayment: tokenized card null")
            }
        } ?: run {
            Log.d("Make Payment", "makeTokenizedCardPayment: payment data null")
        }
    }

    private fun makeTokenizedPayment(
        secureHash: String,
        currencyCode: String,
        payAmount: String,
        merchantId: String,
        terminalId: String,
        ccv: String,
        cardId: Int,
        customerId: String?,
        customerSessionId: String?
    ) {
        // check internet.
        if (!view.isInternetAvailable) {
            view.showNoInternetDialog()
            return
        }
        view.showProgress()
        // create request.
        val paymentRequest = ManualPaymentRequest()
        val amount = AppUtils.formatPaymentAmountToServer(payAmount)
        paymentRequest.amountTrxn = amount.toString() + ""
        paymentRequest.cardAcceptorIDcode = merchantId
        paymentRequest.cardAcceptorTerminalID = terminalId
        paymentRequest.currencyCodeTrxn = currencyCode
        paymentRequest.cvv2 = ccv
        paymentRequest.iSFromPOS = false
        paymentRequest.systemTraceNr = paymentData?.transactionReferenceNumber
        paymentRequest.MerchantReference = paymentData?.transactionReferenceNumber
        paymentRequest.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn()
        paymentRequest.merchantId = merchantId
        paymentRequest.terminalId = terminalId
        paymentRequest.tokenCardId = cardId.toString()
        paymentRequest.tokenCustomerId = customerId
        paymentRequest.tokenCustomerSession = customerSessionId

        paymentRequest.returnURL = ApiLinks.PAYMENT_LINK
        // create secure hash.
        paymentRequest.secureHash = HashGenerator.encode(
            secureHash,
            paymentRequest.dateTimeLocalTrxn,
            merchantId,
            terminalId
        )
        // make transaction.
        ApiConnection.executePayment(
            paymentRequest,
            object : ApiResponseListener<ManualPaymentResponse> {
                override fun onSuccess(response: ManualPaymentResponse?) {
                    if (isViewDetached) return
                    // server make response.
                    view.dismissProgress()
                    if (response?.challengeRequired == true) {
                        view.show3dpWebView(response.threeDSUrl, paymentData)
                    } else {
                        if (response?.mWActionCode != null) {
                            val transactionException = TransactionException()
                            transactionException.errorMessage = response.mWMessage
                            TransactionManager.setTransactionException(transactionException)
                            val bundle = Bundle()
                            bundle.putString(
                                AppConstant.BundleKeys.DECLINE_CAUSE,
                                response.mWMessage
                            )
                            bundle.putString("opened_by", "manual_payment")
                            view.showPaymentFailedFragment(bundle)
                        } else {
                            if (response?.actionCode == null || response.actionCode.isEmpty() || response.actionCode != "000") {
                                val transactionException = TransactionException()
                                transactionException.errorMessage = response?.message
                                TransactionManager.setTransactionException(transactionException)
                                val bundle = Bundle()
                                bundle.putString(
                                    AppConstant.BundleKeys.DECLINE_CAUSE,
                                    response?.message
                                )
                                bundle.putString("opened_by", "manual_payment")
                                view.showPaymentFailedFragment(bundle)
                            } else {
                                // transaction success.
                                val cardTransaction = SuccessfulCardTransaction()
                                cardTransaction.ActionCode = response.actionCode
                                cardTransaction.AuthCode = response.authCode
                                cardTransaction.MerchantReference = response.merchantReference
                                cardTransaction.Message = response.message
                                cardTransaction.NetworkReference = response.networkReference
                                cardTransaction.ReceiptNumber = response.receiptNumber
                                cardTransaction.SystemReference =
                                    response.systemReference.toString() + ""
                                cardTransaction.Success = response.success
                                cardTransaction.merchantId = paymentData?.merchantId
                                cardTransaction.terminalId = paymentData?.terminalId
                                cardTransaction.amount = paymentData?.executedTransactionAmount
                                cardTransaction.tokenCustomerId = paymentData?.customerId
                                TransactionManager.setCardTransaction(cardTransaction)
                                view.showTransactionApprovedFragment(
                                    transactionNo = response.transactionNo,
                                    authCode = response.authCode,
                                    receiptNumber = response.receiptNumber,
                                    cardHolder = "cardHolder",
                                    cardNumber = "cardNumber",
                                    systemReference = response.systemReference.toString() + "",
                                    paymentData = paymentData
                                )
                            }
                        }
                    }
                }

                override fun onFail(error: Throwable) {
                    // payment failed.
                    if (isViewDetached) return
                    view.dismissProgress()
                    val transactionException = TransactionException()
                    transactionException.errorMessage = error.message
                    TransactionManager.setTransactionException(transactionException)
                    error.printStackTrace()
                    view.showErrorInServerToast()
                }
            })
    }
}
