package io.paysky.paybutton.ui.fragment.qr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;

import io.paysky.paybutton.R;

import io.paysky.paybutton.data.model.PaymentData;
import io.paysky.paybutton.data.model.request.QrGeneratorRequest;
import io.paysky.paybutton.data.model.request.RequestToPayRequest;
import io.paysky.paybutton.data.model.request.TransactionStatusRequest;
import io.paysky.paybutton.data.model.response.GenerateQrCodeResponse;
import io.paysky.paybutton.data.model.response.RequestToPayResponse;
import io.paysky.paybutton.data.model.response.TransactionStatusResponse;
import io.paysky.paybutton.data.network.ApiConnection;
import io.paysky.paybutton.data.network.ApiResponseListener;
import io.paysky.paybutton.ui.activity.payment.PaymentActivity;
import io.paysky.paybutton.ui.mvp.BasePresenter;
import io.paysky.paybutton.util.AppConstant;
import io.paysky.paybutton.util.AppUtils;
import io.paysky.paybutton.util.ConvertQrCodToBitmapTask;
import io.paysky.paybutton.util.HashGenerator;
import io.paysky.paybutton.util.QrBitmapLoadListener;
import io.paysky.paybutton.util.TransactionManager;

public class QrPresenter extends BasePresenter<QrView> implements QrBitmapLoadListener {


    private final PaymentData paymentData;
    private String qrCode;
    private long transactionId;

    QrPresenter(Bundle arguments) {
        paymentData = arguments.getParcelable(AppConstant.BundleKeys.PAYMENT_DATA);
        TransactionManager.setTransactionType(TransactionManager.TransactionType.QR);
    }

    public void checkPaymentApproval(long transactionId) {
        // create request.
        TransactionStatusRequest request = new TransactionStatusRequest();
        request.merchantId = paymentData.merchantId;
        request.terminalId = paymentData.terminalId;
        request.txnId = transactionId;
        request.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        request.secureHash = HashGenerator.encode(paymentData.secureHashKey, request.dateTimeLocalTrxn, paymentData.merchantId, paymentData.terminalId);
        // call api.
        ApiConnection.checkTransactionPaymentStatus(request, new ApiResponseListener<TransactionStatusResponse>() {
            @Override
            public void onSuccess(TransactionStatusResponse response) {
                if (isViewDetached())return;
                if (response.success) {
                    // payment success.
                    if (response.isPaid) {
                        view.setPaymentApproved(response, paymentData);
                    } else {
                        view.listenToPaymentApproval();
                    }
                } else {
                    view.listenToPaymentApproval();
                }
            }

            @Override
            public void onFail(Throwable error) {
                if (isViewDetached())return;
                error.printStackTrace();
                view.listenToPaymentApproval();
            }
        });
    }

    public void generateQrCode() {
        if (!view.isInternetAvailable()) {
            view.showNoInternetDialog();
            return;
        }
        view.showProgress();
        QrGeneratorRequest qrGeneratorRequest = new QrGeneratorRequest();
        qrGeneratorRequest.Amount = paymentData.amountFormatted;
        qrGeneratorRequest.MerchantId = paymentData.merchantId;
        qrGeneratorRequest.TerminalId = paymentData.terminalId;
        qrGeneratorRequest.tahweelQR = true;
        qrGeneratorRequest.mVisaQR = true;
        qrGeneratorRequest.MerchantReference = paymentData.transactionReferenceNumber;
        qrGeneratorRequest.DateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        qrGeneratorRequest.SecureHash = HashGenerator.encode(paymentData.secureHashKey, qrGeneratorRequest.DateTimeLocalTrxn, paymentData.merchantId, paymentData.terminalId);

        ApiConnection.generateQrCode(qrGeneratorRequest, new ApiResponseListener<GenerateQrCodeResponse>() {
            @Override
            public void onSuccess(GenerateQrCodeResponse response) {
                if (isViewDetached())return;
                view.dismissProgress();
                if (response.success) {
                    qrCode = response.iSOQR;
                    transactionId = response.txnId;
                    view.showProgress();
                    float sizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            300, view.getContext().getResources().getDisplayMetrics());
                    new ConvertQrCodToBitmapTask(QrPresenter.this, (int) sizePx).execute(response.iSOQR);
                    view.setGenerateQrSuccess(response.txnId);
                } else {
                    view.showInfoToast(response.message);
                }
            }

            @Override
            public void onFail(Throwable error) {
                if (isViewDetached())return;
                view.dismissProgress();
                error.printStackTrace();
                view.showErrorInServerToast();
            }
        });
    }

    public void requestToPay(String mobileNumber) {
        if (!view.isInternetAvailable()) {
            view.showNoInternetDialog();
            return;
        }
        view.showProgress();
        RequestToPayRequest requestToPayRequest = new RequestToPayRequest();
        requestToPayRequest.dateTimeLocalTrxn = AppUtils.getDateTimeLocalTrxn();
        requestToPayRequest.iSOQR = qrCode;
        requestToPayRequest.merchantId = paymentData.merchantId;
        requestToPayRequest.terminalId = paymentData.terminalId;
        requestToPayRequest.txnId = transactionId;
        requestToPayRequest.mobileNumber = mobileNumber;
        requestToPayRequest.merchantReference = paymentData.transactionReferenceNumber;
        // generate hashing.
        requestToPayRequest.secureHash = HashGenerator.encode(paymentData.secureHashKey, requestToPayRequest.dateTimeLocalTrxn, paymentData.merchantId, paymentData.terminalId);
        ApiConnection.requestToPay(requestToPayRequest, new ApiResponseListener<RequestToPayResponse>() {
            @Override
            public void onSuccess(RequestToPayResponse response) {
                if (isViewDetached())return;
                view.dismissProgress();
                if (response.success) {
                    view.disableR2pViews();
                    view.showToast(R.string.sent_request_success);
                    view.listenToPaymentApproval();
                } else {
                    view.showInfoToast(response.message);
                }
            }

            @Override
            public void onFail(Throwable error) {
                if (isViewDetached())return;
                view.dismissProgress();
                error.printStackTrace();
                view.showErrorInServerToast();
            }
        });
    }

    @Override
    public void onLoadBitmapQrSuccess(Bitmap bitmap) {
        if (isViewDetached())return;
        view.dismissProgress();
        PaymentActivity.qrBitmap = bitmap;
        view.showQrImage(bitmap);
    }

    @Override
    public void onLoadBitmapQrFailed() {
        view.dismissProgress();
        view.showErrorInServerToast();
    }
}
