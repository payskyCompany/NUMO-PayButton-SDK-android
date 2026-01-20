package io.paysky.paybutton.ui.fragment.paymentprocessing

import android.os.Bundle
import io.paysky.paybutton.data.model.PaymentData
import io.paysky.paybutton.ui.mvp.BaseView

interface PaymentProcessingView : BaseView {
    fun show3dpWebView(paymentServerURL: String, paymentData: PaymentData?)
    fun showErrorInServerToast()
    fun showPaymentFailedFragment(bundle: Bundle)
    fun showTransactionApprovedFragment(
        transactionNo: String,
        authCode: String,
        receiptNumber: String,
        cardHolder: String,
        cardNumber: String,
        systemReference: String,
        paymentData: PaymentData?
    )
}
