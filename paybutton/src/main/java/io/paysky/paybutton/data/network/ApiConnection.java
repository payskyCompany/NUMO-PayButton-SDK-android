package io.paysky.paybutton.data.network;

import android.util.Log;


import java.util.List;
import java.util.concurrent.TimeUnit;

import io.paysky.paybutton.BuildConfig;
import io.paysky.paybutton.data.model.request.CheckTransactionStatusRequest;
import io.paysky.paybutton.data.model.request.GetSessionRequest;
import io.paysky.paybutton.data.model.request.ListSavedCardsRequest;
import io.paysky.paybutton.data.model.request.ManualPaymentRequest;
import io.paysky.paybutton.data.model.request.MerchantInfoRequest;
import io.paysky.paybutton.data.model.request.QrGeneratorRequest;
import io.paysky.paybutton.data.model.request.RequestToPayRequest;
import io.paysky.paybutton.data.model.request.SendReceiptByMailRequest;
import io.paysky.paybutton.data.model.request.TransactionStatusRequest;
import io.paysky.paybutton.data.model.request.UpdateCardsRequest;
import io.paysky.paybutton.data.model.response.CheckTransactionStatusResponse;
import io.paysky.paybutton.data.model.response.DateTransactionsItem;
import io.paysky.paybutton.data.model.response.GenerateQrCodeResponse;
import io.paysky.paybutton.data.model.response.GetSessionResponse;
import io.paysky.paybutton.data.model.response.ListSavedCardsResponse;
import io.paysky.paybutton.data.model.response.ManualPaymentResponse;
import io.paysky.paybutton.data.model.response.MerchantInfoResponse;
import io.paysky.paybutton.data.model.response.RequestToPayResponse;
import io.paysky.paybutton.data.model.response.SendReceiptByMailResponse;
import io.paysky.paybutton.data.model.response.TransactionStatusResponse;
import io.paysky.paybutton.data.model.response.TransactionsItem;
import io.paysky.paybutton.data.model.response.UpdateCardsResponse;
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

    public static void getSession(GetSessionRequest getSessionRequest,
                                  final ApiResponseListener<GetSessionResponse> listener) {
        createConnection().getSession(getSessionRequest)
                .enqueue(new Callback<GetSessionResponse>() {
                             @Override
                             public void onResponse(Call<GetSessionResponse> call,
                                                    Response<GetSessionResponse> response) {
                                 if (response.isSuccessful()) {
                                     listener.onSuccess(response.body());
                                 }
                             }

                             @Override
                             public void onFailure(Call<GetSessionResponse> call, Throwable t) {
                                 listener.onFail(t);
                             }
                         }
                );
    }

    public static void listSavedCards(ListSavedCardsRequest request,
                                      final ApiResponseListener<ListSavedCardsResponse> listener) {
        createConnection().listSavedCards(request).enqueue(new Callback<ListSavedCardsResponse>() {
            @Override
            public void onResponse(Call<ListSavedCardsResponse> call, Response<ListSavedCardsResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ListSavedCardsResponse> call, Throwable t) {
                listener.onFail(t);
            }
        });
    }

    public static void changeDefaultToken(UpdateCardsRequest request,
                                          final ApiResponseListener<UpdateCardsResponse> listener) {
        createConnection().changeDefaultToken(request).enqueue(new Callback<UpdateCardsResponse>() {
            @Override
            public void onResponse(Call<UpdateCardsResponse> call, Response<UpdateCardsResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<UpdateCardsResponse> call, Throwable t) {
                listener.onFail(t);
            }
        });
    }

    public static void deleteTokenizedCard(UpdateCardsRequest request,
                                           final ApiResponseListener<UpdateCardsResponse> listener) {
        createConnection().deleteTokenizedCard(request).enqueue(new Callback<UpdateCardsResponse>() {
            @Override
            public void onResponse(Call<UpdateCardsResponse> call, Response<UpdateCardsResponse> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<UpdateCardsResponse> call, Throwable t) {
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
        // Endpoint logging interceptor - logs the endpoint being called
        Interceptor endpointLogger = chain -> {
            Request request = chain.request();
            if (BuildConfig.DEBUG) {
                Log.d("ApiConnection", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                Log.d("ApiConnection", "📍 ENDPOINT: " + request.method() + " " + request.url());
                Log.d("ApiConnection", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }
            return chain.proceed(request);
        };

        // Language header interceptor
        Interceptor languageInterceptor = chain -> {
            Request originalRequest = chain.request();
            Request request = originalRequest.newBuilder()
                    .addHeader("Accept-Language", LANG).build();
            if (BuildConfig.DEBUG) {
                Log.d("ApiConnection", "🌐 Accept-Language header: " + LANG);
            }
            return chain.proceed(request);
        };

        // Enhanced logging interceptor for debug mode
        // Create custom logger for better formatting
        HttpLoggingInterceptor.Logger logger = new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                // Log each line separately for better readability
                if (message != null && !message.trim().isEmpty()) {
                    String[] lines = message.split("\n");
                    for (String line : lines) {
                        if (!line.trim().isEmpty()) {
                            Log.d("ApiConnection", line);
                        }
                    }
                }
            }
        };
        
        // Create logging interceptor with custom logger (constructor takes Logger in 3.12.0)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logger);
        
        // Set logging level based on build type
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            Log.d("ApiConnection", "========================================");
            Log.d("ApiConnection", "=== API Connection Created ===");
            Log.d("ApiConnection", "Base URL: " + ApiLinks.PAYMENT_LINK);
            Log.d("ApiConnection", "Language: " + LANG);
            Log.d("ApiConnection", "Logging Level: BODY (Full Request/Response)");
            Log.d("ApiConnection", "========================================");
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        // Build OkHttpClient with interceptors
        // Order matters: endpoint logger -> language header -> logging interceptor
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(endpointLogger)  // First: Log endpoint
                .addInterceptor(languageInterceptor)  // Second: Add language header
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        // Add logging interceptor - always add it, but it only logs in DEBUG mode
        // Using addNetworkInterceptor to log actual network calls (after redirects, retries, etc.)
        clientBuilder.addNetworkInterceptor(loggingInterceptor);

        OkHttpClient client = clientBuilder.build();

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
