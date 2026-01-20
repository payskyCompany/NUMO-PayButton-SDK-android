package io.paysky.paybutton.data.network;

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
import io.paysky.paybutton.data.model.response.GenerateQrCodeResponse;
import io.paysky.paybutton.data.model.response.GetSessionResponse;
import io.paysky.paybutton.data.model.response.ListSavedCardsResponse;
import io.paysky.paybutton.data.model.response.ManualPaymentResponse;
import io.paysky.paybutton.data.model.response.MerchantInfoResponse;
import io.paysky.paybutton.data.model.response.RequestToPayResponse;
import io.paysky.paybutton.data.model.response.SendReceiptByMailResponse;
import io.paysky.paybutton.data.model.response.TransactionStatusResponse;
import io.paysky.paybutton.data.model.response.UpdateCardsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Paysky-202 on 5/17/2018.
 */

public interface ApiInterface {


    @POST(ApiLinks.SEND_RECEIPT_BY_MAIL)
    Call<SendReceiptByMailResponse> sendReceiptByMail(@Body SendReceiptByMailRequest mailRequest);

    @POST(ApiLinks.CHECK_PAYMENT_STATUS)
    Call<TransactionStatusResponse> checkTransactionStatus(@Body TransactionStatusRequest request);

    @POST(ApiLinks.GENERATE_QRCODE)
    Call<GenerateQrCodeResponse> generateQrCode(@Body QrGeneratorRequest request);

    @POST(ApiLinks.EXECUTE_PAYMENT)
    Call<ManualPaymentResponse> executeManualPayment(@Body ManualPaymentRequest paymentRequest);

    @POST(ApiLinks.SMS_PAYMENT)
    Call<RequestToPayResponse> requestToPay(@Body RequestToPayRequest request);

    @POST(ApiLinks.MERCHANT_INFO)
    Call<MerchantInfoResponse> getMerchantInfo(@Body MerchantInfoRequest request);

    @Headers("Content-Type: application/json")
    @POST(ApiLinks.Get_Session_For_Customer_Token)
    Call<GetSessionResponse> getSession(@Body GetSessionRequest request);

    @Headers("Content-Type: application/json")
    @POST(ApiLinks.LIST_SAVED_CARDS_FOR_CUSTOMER)
    Call<ListSavedCardsResponse> listSavedCards(@Body ListSavedCardsRequest request);
//
//    @POST(ApiLinks.COMPOSE_3DS_TRANSACTION)
//    Call<Compose3dsTransactionResponse> compose3dpsTransaction(@Body Compose3dsTransactionRequest request);

//    @POST(ApiLinks.PROCESS_3D_TRANSACTION)
//    Call<Process3dTransactionResponse> process3dTransaction(@Body Process3dTransactionRequest request);

    @POST(ApiLinks.CHANGE_DEFAULT_CARD)
    Call<UpdateCardsResponse> changeDefaultToken(@Body UpdateCardsRequest request);

    @POST(ApiLinks.DELETE_TOKENIZED_CARD)
    Call<UpdateCardsResponse> deleteTokenizedCard(@Body UpdateCardsRequest request);

    @POST(ApiLinks.CHECK_TRANSACTION_STATUS)
    Call<CheckTransactionStatusResponse> checkTransaction(@Body CheckTransactionStatusRequest request);
}
