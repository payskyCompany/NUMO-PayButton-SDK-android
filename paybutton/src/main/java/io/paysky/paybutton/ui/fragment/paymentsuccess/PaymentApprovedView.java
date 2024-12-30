package io.paysky.paybutton.ui.fragment.paymentsuccess;

import android.content.Context;

import androidx.annotation.StringRes;

import io.paysky.paybutton.ui.mvp.BaseView;

interface PaymentApprovedView extends BaseView {
    void setSendEmailSuccess(String emailTo, int operationType);

    Context getContext();

    void showErrorToast(@StringRes int error);
}
