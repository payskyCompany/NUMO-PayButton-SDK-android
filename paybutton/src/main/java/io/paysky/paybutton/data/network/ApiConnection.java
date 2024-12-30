package io.paysky.paybutton.data.network;

import android.util.Log;


import java.util.List;
import java.util.concurrent.TimeUnit;

import io.paysky.paybutton.BuildConfig;
import io.paysky.paybutton.data.model.request.CheckTransactionStatusRequest;
import io.paysky.paybutton.data.model.request.ManualPaymentRequest;
import io.paysky.paybutton.data.model.request.MerchantInfoRequest;
import io.paysky.paybutton.data.model.request.QrGeneratorRequest;
import io.paysky.paybutton.data.model.request.RequestToPayRequest;
import io.paysky.paybutton.data.model.request.SendReceiptByMailRequest;
import io.paysky.paybutton.data.model.request.TransactionStatusRequest;
import io.paysky.paybutton.data.model.response.CheckTransactionStatusResponse;
import io.paysky.paybutton.data.model.response.DateTransactionsItem;
import io.paysky.paybutton.data.model.response.GenerateQrCodeResponse;
import io.paysky.paybutton.data.model.response.ManualPaymentResponse;
import io.paysky.paybutton.data.model.response.MerchantInfoResponse;
import io.paysky.paybutton.data.model.response.RequestToPayResponse;
import io.paysky.paybutton.data.model.response.SendReceiptByMailResponse;
import io.paysky.paybutton.data.model.response.TransactionStatusResponse;
import io.paysky.paybutton.data.model.response.TransactionsItem;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Paysky-202 on 5/14/2018.
 */

public class ApiConnection {


    public static String LANG = "en";

    public static void executePayment(ManualPaymentRequest manualPaymentRequest, final ApiResponseListener<ManualPaymentResponse> listener) {
        createConnection().executeManualPayment(manualPaymentRequest)
                .enqueue(new Callback<ManualPaymentResponse>() {
                    @Override
                    public void onResponse(Call<ManualPaymentResponse> call, Response<ManualPaymentResponse> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body());
                        } else {
                            onFailure(call, new Exception("fail to connect response code = " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ManualPaymentResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }

    public static void sendReceiptByMail(SendReceiptByMailRequest mailRequest, final ApiResponseListener<SendReceiptByMailResponse> listener) {
        createConnection().sendReceiptByMail(mailRequest)
                .enqueue(new Callback<SendReceiptByMailResponse>() {
                    @Override
                    public void onResponse(Call<SendReceiptByMailResponse> call, Response<SendReceiptByMailResponse> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body());
                        } else {
                            onFailure(call, new Exception("fail to connect response code = " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<SendReceiptByMailResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }

    public static void generateQrCode(QrGeneratorRequest request, final ApiResponseListener<GenerateQrCodeResponse> listener) {
        createConnection().generateQrCode(request)
                .enqueue(new Callback<GenerateQrCodeResponse>() {
                    @Override
                    public void onResponse(Call<GenerateQrCodeResponse> call, Response<GenerateQrCodeResponse> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body());
                        } else {
                            onFailure(call, new Exception("fail to connect response code = " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<GenerateQrCodeResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }

    public static void checkTransactionPaymentStatus(TransactionStatusRequest request, final ApiResponseListener<TransactionStatusResponse> listener) {
        createConnection().checkTransactionStatus(request)
                .enqueue(new Callback<TransactionStatusResponse>() {
                    @Override
                    public void onResponse(Call<TransactionStatusResponse> call, Response<TransactionStatusResponse> response) {
                        if (response.isSuccessful()) {
                            listener.onSuccess(response.body());
                        } else {
                            onFailure(call, new Exception("fail to connect response code = " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionStatusResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }


    public static void requestToPay(RequestToPayRequest requestToPayRequest, final ApiResponseListener<RequestToPayResponse> listener) {
        createConnection().requestToPay(requestToPayRequest)
                .enqueue(new Callback<RequestToPayResponse>() {
                    @Override
                    public void onResponse(Call<RequestToPayResponse> call, Response<RequestToPayResponse> response) {
                        listener.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<RequestToPayResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }


    private static ApiInterface createConnection() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        Interceptor interceptor1 = chain -> {
            Request request = chain.request().newBuilder()
                    .addHeader("Accept-Language", LANG).build();
            Log.d("Accept-Language", LANG);
            return chain.proceed(request);
        };


        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .addInterceptor(interceptor1)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();
        Log.d("ApiLinksPAYMENT_LINK", ApiLinks.PAYMENT_LINK);


        return new Retrofit.Builder()
                .baseUrl(ApiLinks.PAYMENT_LINK)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
    }

    public static void getMerchantInfo(MerchantInfoRequest request, final ApiResponseListener<MerchantInfoResponse> listener) {
        createConnection().getMerchantInfo(request)
                .enqueue(new Callback<MerchantInfoResponse>() {
                    @Override
                    public void onResponse(Call<MerchantInfoResponse> call, Response<MerchantInfoResponse> response) {
                        listener.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<MerchantInfoResponse> call, Throwable t) {
                        listener.onFail(t);
                    }
                });
    }

//    public static void compose3dsTransaction(Compose3dsTransactionRequest request, final ApiResponseListener<Compose3dsTransactionResponse> listener) {
//        createConnection().compose3dpsTransaction(request)
//                .enqueue(new Callback<Compose3dsTransactionResponse>() {
//                    @Override
//                    public void onResponse(Call<Compose3dsTransactionResponse> call, Response<Compose3dsTransactionResponse> response) {
//                        listener.onSuccess(response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<Compose3dsTransactionResponse> call, Throwable t) {
//                        listener.onFail(t);
//                    }
//                });
//    }
//
//    public static void process3dTransaction(Process3dTransactionRequest request, final ApiResponseListener<Process3dTransactionResponse> listener) {
//        createConnection().process3dTransaction(request).enqueue(new Callback<Process3dTransactionResponse>() {
//            @Override
//            public void onResponse(Call<Process3dTransactionResponse> call, Response<Process3dTransactionResponse> response) {
//                listener.onSuccess(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<Process3dTransactionResponse> call, Throwable t) {
//                listener.onFail(t);
//            }
//        });
//    }


    public static void checkTransactionStatus(final String transactionId, final CheckTransactionStatusRequest request, final CheckTransactionListener listener) {
        createConnection().checkTransaction(request)
                .enqueue(new Callback<CheckTransactionStatusResponse>() {
                    @Override
                    public void onResponse(Call<CheckTransactionStatusResponse> call, Response<CheckTransactionStatusResponse> response) {
                        CheckTransactionStatusResponse body = response.body();
                        if (body != null && body.success) {
                            boolean transactionSuccess = false;
                            List<TransactionsItem> transactions = body.transactions;
                            for (TransactionsItem item : transactions) {
                                for (DateTransactionsItem transactionsItem : item.dateTransactions) {
                                    if (transactionsItem.merchantReference.equals(transactionId)) {
                                        transactionSuccess = true;
                                        listener.transactionSuccess(transactionsItem);
                                    }
                                }
                            }
                            if (!transactionSuccess) {
                                listener.transactionFailed();
                            }
                        } else {
                            listener.onError(new Exception("error in server"));
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckTransactionStatusResponse> call, Throwable t) {
                        listener.onError(t);
                    }
                });
    }

}
