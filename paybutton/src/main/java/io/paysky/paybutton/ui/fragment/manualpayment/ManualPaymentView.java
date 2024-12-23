package io.paysky.paybutton.ui.fragment.manualpayment;

import android.os.Bundle;

import io.paysky.paybutton.data.model.PaymentData;
import io.paysky.paybutton.ui.mvp.BaseView;

public interface ManualPaymentView extends BaseView {


    void show3dpWebView(String paymentServerURL , PaymentData paymentData);

    void showErrorInServerToast();

    void showPaymentFailedFragment(Bundle bundle);

    void showTransactionApprovedFragment(String transactionNo, String authCode, String receiptNumber, String cardHolder, String cardNumber, String systemReference, PaymentData paymentData);
}
